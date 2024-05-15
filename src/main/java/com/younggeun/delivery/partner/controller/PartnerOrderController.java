package com.younggeun.delivery.partner.controller;

import com.younggeun.delivery.partner.service.PartnerOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/partners/order")
@RequiredArgsConstructor
public class PartnerOrderController {
  private final PartnerOrderService orderService;

  @Operation(summary = "오늘 주문 목록 조회", description = "authentication")
  @GetMapping("/{storeId}")
  public ResponseEntity<?> getOrderList(Authentication authentication, Pageable pageable,
      @PathVariable String storeId) {
    var result = orderService.getPartnerOrderList(authentication, storeId, pageable);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "주문 상세 조회", description = "authentication")
  @GetMapping("/{storeId}/{orderId}")
  public ResponseEntity<?> getOrder(Authentication authentication,
      @PathVariable String storeId, @PathVariable String orderId) {
    var result = orderService.getPartnerOrder(authentication, storeId, orderId);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "주문 수락", description = "authentication")
  @PutMapping("/{storeId}/{orderId}/accept")
  public ResponseEntity<?> orderAccess(Authentication authentication,
      @PathVariable String storeId, @PathVariable String orderId) {
    var result = orderService.orderAccess(authentication, orderId, true);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "주문 거절", description = "authentication")
  @PutMapping("/{storeId}/{orderId}/refuse")
  public ResponseEntity<?> orderRefuse(Authentication authentication,
      @PathVariable String storeId, @PathVariable String orderId) {
    var result = orderService.orderAccess(authentication, orderId, false);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "배달 완료", description = "authentication")
  @PutMapping("/{storeId}/{orderId}/complete")
  public ResponseEntity<?> orderComplete(Authentication authentication,
      @PathVariable String storeId, @PathVariable String orderId) {
    var result = orderService.orderComplete(authentication, orderId);

    return ResponseEntity.ok(result);
  }
}
