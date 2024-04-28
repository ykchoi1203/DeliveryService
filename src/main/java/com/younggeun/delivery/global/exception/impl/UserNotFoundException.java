package com.younggeun.delivery.global.exception.impl;

import com.younggeun.delivery.global.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AbstractException {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "해당 이메일의 유저가 존재하지 않습니다.";
  }

}
