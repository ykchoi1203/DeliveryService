package com.younggeun.delivery.user.service;

import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MENU_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.CART_IS_EMPTY;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.CART_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.DIFFERENT_STORE_IN_CART;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.AdditionalMenuRepository;
import com.younggeun.delivery.store.domain.MenuRepository;
import com.younggeun.delivery.store.domain.dto.AdditionalMenuDto;
import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.user.domain.dto.CartDto;
import com.younggeun.delivery.user.domain.dto.CartMenuDto;
import com.younggeun.delivery.user.domain.entity.Cart;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@AllArgsConstructor
@Service
public class ShoppingCartService {
  private final RedisTemplate<String, Object> redisTemplate;
  private final MenuRepository menuRepository;
  private final AdditionalMenuRepository additionalMenuRepository;

  public List<CartDto> getCart(String userEmail) {

    if (Boolean.FALSE.equals(redisTemplate.hasKey(userEmail))) {
      return new ArrayList<>();
    }

    String cartJson = (String) redisTemplate.opsForValue().get(userEmail);

    Cart cart = deserialize(cartJson);

    return cart.getItems();
  }

  public List<CartDto> addToCart(String userEmail, CartDto menu) {

    if (Boolean.FALSE.equals(redisTemplate.hasKey(userEmail))) {
      Cart cart = new Cart();
      cart.setItems(new ArrayList<>());
      redisTemplate.opsForValue().set(userEmail, serialize(cart));
    }

    String cartJson = (String) redisTemplate.opsForValue().get(userEmail);

    Cart cart = deserialize(cartJson);

    if (!cart.getItems().isEmpty()) {
      if (!(cart.getItems().get(0)).getStoreId().equals(menu.getStoreId())) {
        throw new RestApiException(DIFFERENT_STORE_IN_CART);
      }
    }

    menu.setCartId((!cart.getItems().isEmpty() ? (cart.getItems().get(cart.getItems().size()-1)).getCartId() + 1 : 1));

    addMenuToCart(cart, menu);

    redisTemplate.opsForValue().set(userEmail, serialize(cart));

    return getCart(userEmail);
  }

  private void addMenuToCart(Cart cart, CartDto menu) {
    CartMenuDto menuDto = new CartMenuDto(menuRepository.findById(menu.getMenu().getMenuId())
        .orElseThrow(() -> new RestApiException(MENU_NOT_FOUND)));

    if (!cart.getItems().isEmpty()) {
      if (menuDto.getStoreId() != cart.getItems().get(0)
          .getStoreId()) {
        throw new RestApiException(DIFFERENT_STORE_IN_CART);
      }
    }

    menu.setMenu(menuDto);

    List<AdditionalMenu> additionalMenuList = additionalMenuRepository
        .findAllByAdditionalMenuIdIn(menu.getAdditionalMenuList().stream().map(AdditionalMenuDto::getAdditionalMenuId).toList());

    menu.setAdditionalMenuList(additionalMenuList.stream().filter(item -> item.getMenu().getMenuId() == menuDto.getMenuId()).map(AdditionalMenuDto::new).toList());

    menu.setTotalCost(menu.getQuantity() * (menu.getMenu().getPrice() + additionalMenuList.stream().mapToInt(
        AdditionalMenu::getPrice).sum()));

    cart.getItems().add(menu);
  }

  public List<CartDto> removeFromCart(String userEmail, String cartId) {
    if (Boolean.FALSE.equals(redisTemplate.hasKey(userEmail))) {
      throw new RestApiException(CART_IS_EMPTY);
    }

    Cart cart = deserialize((String) redisTemplate.opsForValue().get(userEmail));

    removeMenuFromCart(cart, Long.parseLong(cartId));

    redisTemplate.opsForValue().set(userEmail, serialize(cart));

    return getCart(userEmail);
  }

  private void removeMenuFromCart(Cart cart, long cartId) {
    List<CartDto> list = cart.getItems();
    for(int i=0; i<list.size(); i++) {
      if((list.get(i)).getCartId() == cartId) {
        list.remove(i);
        break;
      }
    }
  }

  public List<CartDto> updateCartMenu(String userEmail, String cartId, CartDto menuDto) {
    if (Boolean.FALSE.equals(redisTemplate.hasKey(userEmail))) {
      throw new RestApiException(CART_IS_EMPTY);
    }
    menuDto.setCartId(Long.parseLong(cartId));

    Cart cart = deserialize((String) redisTemplate.opsForValue().get(userEmail));

    updateCartMenu(cart, menuDto);

    redisTemplate.opsForValue().set(userEmail, serialize(cart));

    return getCart(userEmail);
  }

  private void updateCartMenu(Cart cart, CartDto menu) {
    List<CartDto> list = cart.getItems();

    List<AdditionalMenu> additionalMenuList = additionalMenuRepository
        .findAllByAdditionalMenuIdIn(menu.getAdditionalMenuList().stream().map(AdditionalMenuDto::getAdditionalMenuId).toList());

    list.stream().filter(item -> item.getCartId() == menu.getCartId()).findFirst().map(cartDto -> {
      cartDto.setQuantity(menu.getQuantity());
      cartDto.setAdditionalMenuList(additionalMenuList.stream().map(AdditionalMenuDto::new).toList());
      cartDto.setTotalCost(menu.getQuantity() * (cartDto.getMenu().getPrice() + additionalMenuList.stream().mapToInt(AdditionalMenu::getPrice).sum()));
      return cartDto;
    }).orElseThrow(() -> new RestApiException(CART_NOT_FOUND));

  }

  private String serialize(Cart cart) {
    try {
      return new ObjectMapper().writeValueAsString(cart);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error serializing cart", e);
    }
  }

  private Cart deserialize(String json) {
    try {
      return new ObjectMapper().readValue(json, Cart.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Error deserializing cart", e);
    }
  }
}