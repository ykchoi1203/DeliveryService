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
  ADDITIONAL_MENU_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 추가 메뉴를 찾을 수 없습니다."),
  EXISTS_SEQUENCE_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 존재하는 순번입니다."),
  CANNOT_DELETE_CATEGORY_CAUSE_EXIST_MENU_BY_CATEGORY_ID(HttpStatus.BAD_REQUEST, "해당 카테고리에 존재하는 메뉴가 있습니다. 해당 카테고리의 메뉴를 먼저 삭제해주세요."),
  MISMATCH_PARTNER_STORE(HttpStatus.BAD_REQUEST, "가게에 등록된 파트너와 로그인한 파트너가 일치하지 않습니다."),
  EXIST_PHOTO_EXCEPTION(HttpStatus.BAD_REQUEST, "이미 등록된 사진이 존재합니다."),
  MISMATCH_STORE_CATEGORY(HttpStatus.BAD_REQUEST, "해당 가게와 카테고리의 가게가 일치하지 않습니다."),
  MISMATCH_STORE_MENU(HttpStatus.BAD_REQUEST, "메뉴에 등록된 가게와 가게 ID가 일치하지 않습니다.."),
  ALREADY_EXIST_SEQUENCE(HttpStatus.BAD_REQUEST, "해당 순서로 등록할 수 없습니다.");
  private final HttpStatus httpStatus;
  private final String message;

}