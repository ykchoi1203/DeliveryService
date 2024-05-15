package com.younggeun.delivery.user.service;

import static com.younggeun.delivery.global.exception.type.PayErrorCode.LEAST_ORDER_COST;
import static com.younggeun.delivery.global.exception.type.PayErrorCode.ORDER_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MENU_SOLD_OUT;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_OPENED;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.ADDRESS_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.CART_IS_EMPTY;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_ORDER;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.AdditionalMenuRepository;
import com.younggeun.delivery.store.domain.MenuRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.dto.AdditionalMenuDto;
import com.younggeun.delivery.store.domain.dto.MenuDto;
import com.younggeun.delivery.store.domain.dto.OrderDetailDto;
import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.DeliveryAddressRepository;
import com.younggeun.delivery.user.domain.OrderHistoryRepository;
import com.younggeun.delivery.user.domain.OrderRepository;
import com.younggeun.delivery.user.domain.PaymentHistoryRepository;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.dto.CartDto;
import com.younggeun.delivery.user.domain.dto.CartMenuDto;
import com.younggeun.delivery.user.domain.dto.KakaoApproveResponse;
import com.younggeun.delivery.user.domain.dto.OrderDto;
import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.OrderHistory;
import com.younggeun.delivery.user.domain.entity.OrderTable;
import com.younggeun.delivery.user.domain.entity.PaymentHistory;
import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.type.PaymentType;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class OrderService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final UserRepository userRepository;
  private final MenuRepository menuRepository;
  private final AdditionalMenuRepository additionalMenuRepository;
  private final OrderRepository orderRepository;
  private final StoreRepository storeRepository;
  private final DeliveryAddressRepository addressRepository;
  private final OrderHistoryRepository orderHistoryRepository;
  private final PaymentHistoryRepository paymentHistoryRepository;
  private final ShoppingCartService shoppingCartService;

  public Page<OrderDto> getOrderList(Authentication authentication, Pageable pageable) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new RestApiException(USER_NOT_FOUND_EXCEPTION));
      return orderRepository.findAllByUserUserIdOrderByCreatedAtDesc(user.getUserId(), pageable).map(
          OrderDto::new);
  }

  @Transactional
  public OrderDto saveOrder(Authentication authentication, OrderDto orderDto) {
    List<CartDto> cartDtoList = shoppingCartService.getCart(authentication.getName());
    Long storeId = cartDtoList.stream().map(CartDto::getStoreId).findFirst().orElseThrow(() -> new RestApiException(CART_IS_EMPTY));
    Store store = storeRepository.findById(storeId).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(!store.isOpened()) throw new RestApiException(STORE_NOT_OPENED);

    List<Long> menuIdList = cartDtoList.stream().map(CartDto::getMenu).map(CartMenuDto::getMenuId).toList();
    List<Long> additionalMenuIdList = cartDtoList.stream()
        .flatMap(cartDto -> cartDto.getAdditionalMenuList().stream())
        .map(AdditionalMenuDto::getAdditionalMenuId)
        .toList();

    List<Menu> orderMenuList = menuRepository.findAllByMenuIdIn(menuIdList);

    List<AdditionalMenu> additionalMenuList = additionalMenuRepository.findAllByAdditionalMenuIdIn(additionalMenuIdList);

    int totalCost = cartDtoList.stream().mapToInt(CartDto::getTotalCost).sum();

    if(totalCost < store.getLeastOrderCost()) {
      throw new RestApiException(LEAST_ORDER_COST);
    }

    orderDto.setTotalPrice(totalCost + store.getDeliveryCost());

    if(orderMenuList.stream().anyMatch(Menu::isSoldOutStatus) || additionalMenuList.stream().anyMatch(AdditionalMenu::isSoldOutStatus)) {
      throw new RestApiException(MENU_SOLD_OUT);
    }

    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    DeliveryAddress address = addressRepository.findByUserAndDefaultAddressStatusIsTrue(user).orElseThrow(() -> new RestApiException(ADDRESS_NOT_FOUND));

    OrderTable orderTable= orderRepository.save(OrderTable.builder()
        .totalPrice(orderDto.getTotalPrice())
        .request(orderDto.getRequest())
        .paymentType(PaymentType.KAKAOPAY)
        .status(OrderStatus.WAIT)
        .user(user)
        .store(store)
        .address(address)
        .build()
    );

    List<OrderHistory> orderHistoryList = new ArrayList<>();
    cartDtoList.forEach(cartDto  -> {
      Menu menu = Menu.builder().menuId(cartDto.getMenu().getMenuId()).build();
      int quantity = cartDto.getQuantity();
      orderHistoryList.add(OrderHistory.builder().menu(menu).orderTable(orderTable).quantity(quantity).createdAt(LocalDateTime.now()).build());
      cartDto.getAdditionalMenuList().forEach(additionalMenuDto -> {
        AdditionalMenu additionalMenu = AdditionalMenu.builder().additionalMenuId(additionalMenuDto.getAdditionalMenuId()).build();
        orderHistoryList.add(OrderHistory.builder().menu(menu).additionalMenu(additionalMenu).orderTable(orderTable).quantity(quantity).createdAt(
            LocalDateTime.now()).build());
      });
    });

    orderHistoryRepository.saveAll(orderHistoryList);

    redisTemplate.opsForValue().set("order:"+authentication.getName(), orderTable.getOrderId());

    return new OrderDto(orderTable);
  }

  @Transactional
  public OrderDto createOrder(Authentication authentication, KakaoApproveResponse kakaoApproveResponse) {
    if(!authentication.getName().equals(kakaoApproveResponse.getPartner_user_id())) {
      throw new RestApiException(MISMATCH_USER_EXCEPTION);
    }
    Long orderId =  Long.valueOf((Integer)redisTemplate.opsForValue().get("order:" + authentication.getName()));

    OrderTable orderTable = orderRepository.findById(orderId).orElseThrow(() -> new RestApiException(ORDER_NOT_FOUND));
    orderTable.setStatus(OrderStatus.PAYMENT);
    PaymentHistory paymentHistory = new PaymentHistory(kakaoApproveResponse, orderTable);

    paymentHistoryRepository.save(paymentHistory);
    OrderTable updateOrderTable = orderRepository.save(orderTable);

    // 저장이 완료된 orderId 값 삭제
    redisTemplate.delete("order:"+authentication.getName());

    // 장바구니 비우기
    redisTemplate.delete(authentication.getName());
    return new OrderDto(updateOrderTable);
  }


  public OrderDetailDto getUserOrder(Authentication authentication, String orderId) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(()-> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    OrderTable orderTable = orderRepository.findById(Long.valueOf(orderId)).orElseThrow(() -> new RestApiException(ORDER_NOT_FOUND));

    if(user.getUserId() != orderTable.getUser().getUserId()) {
      throw new RestApiException(MISMATCH_USER_ORDER);
    }

    List<OrderHistory> orderHistoryList = orderHistoryRepository.findAllByOrderTableOrderId(Long.valueOf(orderId));

    List<Long> menuIdList = orderHistoryList.stream().map(OrderHistory::getMenu).filter(Objects::nonNull)
        .map(Menu::getMenuId).distinct().toList();
    List<MenuDto> menuList =  menuRepository.findAllByMenuIdIn(menuIdList).stream().map(MenuDto::new).toList();
    Map<Long, ArrayList<AdditionalMenuDto>> additionalMenuMap = additionalMenuRepository.findAllByMenuMenuIdIn(menuIdList)
        .stream().collect(Collectors.groupingBy(
            additionalMenu -> additionalMenu.getMenu().getMenuId(),
            Collectors.mapping(AdditionalMenuDto::new, Collectors.toCollection(ArrayList::new))
        ));

    menuList.forEach(menuDto -> menuDto.setAdditionalMenuList(additionalMenuMap.get(menuDto.getMenuId())));

    return new OrderDetailDto(orderTable, menuList);
  }
}
