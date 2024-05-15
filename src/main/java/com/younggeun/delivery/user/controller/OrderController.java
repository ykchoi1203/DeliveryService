package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.user.domain.dto.KakaoApproveResponse;
import com.younggeun.delivery.user.domain.dto.OrderDto;
import com.younggeun.delivery.user.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users/order")
@RequiredArgsConstructor
public class OrderController {

  private final OrderService orderService;

  @Operation(summary = "주문 목록 조회", description = "authentication")
  @GetMapping
  public ResponseEntity<?> getOrderList(Authentication authentication, Pageable pageable) {
    var result = this.orderService.getOrderList(authentication, pageable);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "주문하기", description = "authentication")
  @PostMapping
  public String createOrder(Authentication authentication, @RequestBody OrderDto orderDto) {
    var result = this.orderService.saveOrder(authentication, orderDto);
    return "redirect:http://localhost:8080/users/payment/kakao/ready";
  }

  @Operation(summary = "주문하기", description = "authentication")
  @PostMapping("/success")
  public ResponseEntity<?> createOrder(Authentication authentication, @RequestBody KakaoApproveResponse kakaoApproveResponse) {
    var result = this.orderService.createOrder(authentication, kakaoApproveResponse);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "주문 상세 조회", description = "authentication")
  @GetMapping("/{orderId}")
  public ResponseEntity<?> getOrder(Authentication authentication, @PathVariable String orderId) {
    var result = orderService.getUserOrder(authentication, orderId);

    return ResponseEntity.ok(result);
  }
}
