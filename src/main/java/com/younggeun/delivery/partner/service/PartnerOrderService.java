package com.younggeun.delivery.partner.service;

import static com.younggeun.delivery.global.exception.type.PayErrorCode.ORDER_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.CANNOT_CHANGE_ORDER;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_PARTNER_ORDER;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_PARTNER_STORE;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.partner.domain.PartnerRepository;
import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.AdditionalMenuRepository;
import com.younggeun.delivery.store.domain.MenuRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.dto.AdditionalMenuDto;
import com.younggeun.delivery.store.domain.dto.MenuDto;
import com.younggeun.delivery.store.domain.dto.OrderDetailDto;
import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.OrderHistoryRepository;
import com.younggeun.delivery.user.domain.OrderRepository;
import com.younggeun.delivery.user.domain.dto.OrderDto;
import com.younggeun.delivery.user.domain.entity.OrderHistory;
import com.younggeun.delivery.user.domain.entity.OrderTable;
import java.time.LocalDate;
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
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PartnerOrderService {
  private final PartnerRepository partnerRepository;
  private final StoreRepository storeRepository;
  private final OrderRepository orderRepository;
  private final MenuRepository menuRepository;
  private final AdditionalMenuRepository additionalMenuRepository;
  private final OrderHistoryRepository orderHistoryRepository;
  public Page<OrderDto> getPartnerOrderList(
      Authentication authentication, String storeId, Pageable pageable) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    Store store = storeRepository.findById(Long.valueOf(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(partner.getPartnerId() != store.getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    Page<OrderTable> orderTableList = orderRepository.findAllByStoreAndStatusGreaterThanAndUpdatedAtBetweenOrderByUpdatedAtDesc(store, OrderStatus.WITHDRAW,
                                              LocalDateTime.of(LocalDate.now(), store.getOpenTime()),
                                              LocalDateTime.of(store.getEndTime().isBefore(store.getOpenTime()) ?
                                                  LocalDate.now().plusDays(1) : LocalDate.now(), store.getEndTime()), pageable);

    return orderTableList.map(OrderDto::new);

  }

  public OrderDetailDto getPartnerOrder(Authentication authentication, String storeId, String orderId) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    Store store = storeRepository.findById(Long.valueOf(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(partner.getPartnerId() != store.getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    OrderTable orderTable = orderRepository.findById(Long.valueOf(orderId)).orElseThrow(() -> new RestApiException(ORDER_NOT_FOUND));
    List<OrderHistory> orderHistoryList = orderHistoryRepository.findAllByOrderTableOrderId(orderTable.getOrderId());
    List<Long> menuIdList = orderHistoryList.stream().map(OrderHistory::getMenu).filter(Objects::nonNull).map(Menu::getMenuId).distinct().toList();
    List<MenuDto> menuList =  menuRepository.findAllByMenuIdIn(menuIdList).stream().map(MenuDto::new).toList();

    Map<Long, ArrayList<AdditionalMenuDto>> additionalMenuMap = additionalMenuRepository.findAllByMenuMenuIdIn(menuIdList)
        .stream().collect(Collectors.groupingBy(
            additionalMenu -> additionalMenu.getMenu().getMenuId(),
            Collectors.mapping(AdditionalMenuDto::new, Collectors.toCollection(ArrayList::new))
        ));

    menuList.forEach(menuDto -> menuDto.setAdditionalMenuList(additionalMenuMap.get(menuDto.getMenuId())));

    return new OrderDetailDto(orderTable, menuList);

  }

  public OrderDto orderAccess(Authentication authentication, String orderId, boolean isAccess) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    OrderTable orderTable = orderRepository.findById(Long.valueOf(orderId)).orElseThrow(() -> new RestApiException(ORDER_NOT_FOUND));

    if(partner.getPartnerId() != orderTable.getStore().getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_ORDER);
    }

    if(orderTable.getStatus() != OrderStatus.PAYMENT) {
      throw new RestApiException(CANNOT_CHANGE_ORDER);
    }

    orderTable.setStatus(isAccess ? OrderStatus.ACCEPT : OrderStatus.REFUSE);

    return new OrderDto(orderRepository.save(orderTable));

  }

  public Object orderComplete(Authentication authentication, String orderId) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    OrderTable orderTable = orderRepository.findById(Long.valueOf(orderId)).orElseThrow(() -> new RestApiException(ORDER_NOT_FOUND));

    if(partner.getPartnerId() != orderTable.getStore().getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_ORDER);
    }

    if(orderTable.getStatus() != OrderStatus.ACCEPT) {
      throw new RestApiException(CANNOT_CHANGE_ORDER);
    }

    orderTable.setStatus(OrderStatus.DELIVERY_COMPLETED);
    orderTable.setDeliveryTime(LocalDateTime.now());

    return new OrderDto(orderRepository.save(orderTable));
  }
}
