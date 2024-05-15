package com.younggeun.delivery.user.domain.dto;

import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.OrderTable;
import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.type.PaymentType;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
  private Long orderId;
  private int totalPrice;
  private OrderStatus status;
  private String request;
  private LocalDateTime deliveryTime;
  private PaymentType paymentType;
  private Long addressId;
  private Long userId;
  private Long storeId;

  public OrderDto(OrderTable orderTable) {
    this.orderId = orderTable.getOrderId();
    this.totalPrice = orderTable.getTotalPrice();
    this.status = orderTable.getStatus();
    this.request = orderTable.getRequest();
    this.deliveryTime = orderTable.getDeliveryTime();
    this.paymentType = orderTable.getPaymentType();
    this.addressId = orderTable.getAddress().getAddressId();
    this.storeId = orderTable.getStore().getStoreId();
    this.userId = orderTable.getUser().getUserId();
  }

  public OrderDto(KakaoApproveResponse kakaoApproveResponse) {
    this.totalPrice = kakaoApproveResponse.getAmount().getTotal();
    this.status = OrderStatus.PAYMENT;
    this.paymentType = PaymentType.KAKAOPAY;
  }

  public void setOrderDto(KakaoApproveResponse kakaoApproveResponse) {
    this.totalPrice = kakaoApproveResponse.getAmount().getTotal();
    this.status = OrderStatus.PAYMENT;
    this.paymentType = PaymentType.KAKAOPAY;
  }

  public OrderTable toEntity(String request, Store store, User user, DeliveryAddress address) {
    return OrderTable.builder()
        .deliveryTime(null)
        .request(request)
        .address(address)
        .user(user)
        .store(store)
        .status(status)
        .paymentType(paymentType)
        .totalPrice(totalPrice)
        .build();
  }
}
