package com.younggeun.delivery.global.exception.type;

import com.younggeun.delivery.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum StoreErrorCode implements ErrorCode {
  STORE_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 가게를 찾을 수 없습니다."),
  STORE_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 카테고리를 찾을 수 없습니다."),
  MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 메뉴를 찾을 수 없습니다."),
  MISMATCH_STORE_CATEGORY(HttpStatus.BAD_REQUEST, "해당 가게와 카테고리의 가게가 일치하지 않습니다."),
  ALREADY_EXIST_SEQUENCE(HttpStatus.BAD_REQUEST, "해당 순서로 등록할 수 없습니다.");
  private final HttpStatus httpStatus;
  private final String message;

}