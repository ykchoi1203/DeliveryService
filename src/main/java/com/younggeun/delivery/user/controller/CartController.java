package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.user.domain.dto.CartDto;
import com.younggeun.delivery.user.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users/cart")
@RequiredArgsConstructor
public class CartController {
  private final ShoppingCartService shoppingCartService;

  @Operation(summary = "user cart 조회", description = "authentication")
  @GetMapping
  public ResponseEntity<?> getCartList(Authentication authentication) {
    var result = this.shoppingCartService.getCart(authentication.getName());

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user cart 추가", description = "authentication")
  @PostMapping
  public ResponseEntity<?> addMenuToCart(Authentication authentication, @RequestBody CartDto menuDto) {
    var result = this.shoppingCartService.addToCart(authentication.getName(), menuDto);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user cart 수정", description = "authentication")
  @PutMapping("/{cartId}")
  public ResponseEntity<?> updateMenuToCart(Authentication authentication,@PathVariable String cartId, @RequestBody CartDto menuDto) {
    var result = this.shoppingCartService.updateCartMenu(authentication.getName(), cartId, menuDto);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user cart 삭제", description = "authentication")
  @DeleteMapping("/{cartId}")
  public ResponseEntity<?> removeMenuToCart(Authentication authentication, @PathVariable String cartId) {
    var result = this.shoppingCartService.removeFromCart(authentication.getName(), cartId);

    return ResponseEntity.ok(result);
  }


}
