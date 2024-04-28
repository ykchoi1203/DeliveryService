package com.younggeun.delivery.global.exception.impl;

import com.younggeun.delivery.global.exception.AbstractException;
import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends AbstractException {
  @Override
  public int getStatusCode() {
    return HttpStatus.BAD_REQUEST.value();
  }

  @Override
  public String getMessage() {
    return "패스워드가 일치하지 않습니다.";
  }


}
