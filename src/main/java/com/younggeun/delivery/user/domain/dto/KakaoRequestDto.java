package com.younggeun.delivery.user.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoRequestDto {
  private String tid; // 결제 고유 번호
  private String next_redirect_mobile_url; // 모바일 웹일 경우 받는 결제페이지 url
  private String next_redirect_pc_url; // pc 웹일 경우 받는 결제 페이지
  private String created_at;
  private String partnerOrderId;
  private String partnerUserId;
  private String itemName;
  private int quantity;
  private int totalPrice;
  private int vatAmount;
  private int texFreeAmount;
  private String pgToken;
}
