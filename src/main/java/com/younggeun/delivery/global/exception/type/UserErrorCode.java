package com.younggeun.delivery.global.exception.type;

import com.younggeun.delivery.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
  AlreadyExistPhoneNumberException(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 핸드폰 번호입니다."),
  AlreadyExistUserException(HttpStatus.BAD_REQUEST, "이미 등록되어 있는 유저입니다."),
  MISMATCH_USER_EXCEPTION(HttpStatus.BAD_REQUEST, "로그인한 유저와 이메일이 일치하지 않습니다."),
  PasswordMismatchException(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
  UserNotFoundException(HttpStatus.BAD_REQUEST, "해당 유저를 찾을 수 없습니다.");
  private final HttpStatus httpStatus;
  private final String message;

}
