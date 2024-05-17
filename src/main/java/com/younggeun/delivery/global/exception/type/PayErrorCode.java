package com.younggeun.delivery.global.exception.type;

import com.younggeun.delivery.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum PayErrorCode implements ErrorCode {
  KAKOPAY_CANCELED(HttpStatus.BAD_REQUEST, "카카오페이 결제를 취소했습니다."),
  KAKAOPAY_FAILED(HttpStatus.BAD_REQUEST, "카카오페이 결제가 실패했습니다."),
  ORDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "주문 조회에 실패했습니다."),
  LEAST_ORDER_COST(HttpStatus.BAD_REQUEST, "장바구니에 담긴 총 금액이 최소 주문 금액보다 작습니다."),
  KAKAO_PAY_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "장바구니에 담긴 총 금액이 최소 주문 금액보다 작습니다."),;

  private final HttpStatus httpStatus;
  private final String message;
}
