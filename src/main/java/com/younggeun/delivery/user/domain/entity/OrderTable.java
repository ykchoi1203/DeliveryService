package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.type.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Setter
@Entity
public class OrderTable extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long orderId;

  @Column(nullable = false, columnDefinition = "INT CHECK (total_price > 0)")
  private int totalPrice;

  @Enumerated
  @Column(nullable = false)
  private OrderStatus status;
  private String request;
  private LocalDateTime deliveryTime;

  @Enumerated
  @Column(nullable = false)
  private PaymentType paymentType;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "address_id", nullable = false)
  private DeliveryAddress address;

  @ManyToOne
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

}
