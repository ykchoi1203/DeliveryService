package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.entity.OrderTable;
import com.younggeun.delivery.user.domain.type.PaymentType;
import java.time.LocalDateTime;
import java.util.List;
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
public class OrderDetailDto {
  private Long orderId;
  private int totalPrice;
  private OrderStatus status;
  private String request;
  private LocalDateTime deliveryTime;
  private PaymentType paymentType;
  private String address;
  private Long storeId;
  private List<MenuDto> menuDtoList;

  public OrderDetailDto(OrderTable orderTable, List<MenuDto> menuList) {
    this.menuDtoList = menuList;
    this.orderId = orderTable.getOrderId();
    this.totalPrice = orderTable.getTotalPrice();
    this.status = orderTable.getStatus();
    this.request = orderTable.getRequest();
    this.deliveryTime = orderTable.getDeliveryTime();
    this.paymentType = orderTable.getPaymentType();
    this.address = orderTable.getAddress().getAddress1() + " " + orderTable.getAddress().getAddress2() + " " + orderTable.getAddress().getAddress3();
    this.storeId = orderTable.getStore().getStoreId();
  }
}
