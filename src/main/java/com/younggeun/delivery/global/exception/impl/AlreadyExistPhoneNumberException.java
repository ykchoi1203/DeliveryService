package com.younggeun.delivery.global.exception.impl;

import com.younggeun.delivery.global.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class AlreadyExistPhoneNumberException extends AbstractException {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "이미 등록되어 있는 핸드폰 번호입니다.";
  }

}
