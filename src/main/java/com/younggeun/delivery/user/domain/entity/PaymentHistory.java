package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.user.domain.dto.KakaoApproveResponse;
import com.younggeun.delivery.user.domain.type.PaymentStatusType;
import com.younggeun.delivery.user.domain.type.PaymentType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Entity
public class PaymentHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentHistoryId;

  private String tid; // 결제 고유 번호
  private String cid; // 가맹점 코드
  private String partnerUserId;
  private int quantity; // 상품 수량
  private LocalDateTime createdAt; // 결제 요청 시간
  private LocalDateTime approvedAt; // 결제 승인 시간
  private PaymentType paymentType;
  private int totalPrice;
  private PaymentStatusType status;

  @OneToOne
  @JoinColumn(name = "order_id")
  private OrderTable orderTable;

  public PaymentHistory(KakaoApproveResponse response, OrderTable orderTable) {
    this.orderTable = orderTable;
    this.tid = response.getTid();
    this.cid = response.getCid();
    this.partnerUserId = response.getPartner_user_id();
    this.totalPrice = response.getAmount().getTotal();
    this.quantity = response.getQuantity();
    this.createdAt = LocalDateTime.parse(response.getCreated_at());
    this.approvedAt = LocalDateTime.parse(response.getApproved_at());
    this.paymentType = PaymentType.KAKAOPAY;
    this.status = PaymentStatusType.PAYED;
  }
}
