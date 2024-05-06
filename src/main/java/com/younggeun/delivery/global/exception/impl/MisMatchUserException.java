package com.younggeun.delivery.global.exception.impl;

import com.younggeun.delivery.global.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class MisMatchUserException extends AbstractException {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "로그인한 유저와 이메일이 일치하지 않습니다.";
  }

}
