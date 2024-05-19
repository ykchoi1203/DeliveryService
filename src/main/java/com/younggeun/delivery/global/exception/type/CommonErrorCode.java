package com.younggeun.delivery.global.exception.type;

import com.younggeun.delivery.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
@AllArgsConstructor
public enum CommonErrorCode implements ErrorCode {
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not exists"),
  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
  FILE_SAVE_ERROR(HttpStatus.BAD_REQUEST, "사진 저장에 실패했습니다."),
  PHOTO_NOT_FOUND(HttpStatus.BAD_REQUEST, "사진 저장에 실패했습니다."),
  NOT_ALLOW_EXCEPTION(HttpStatus.FORBIDDEN, "허가되지 않은 접근입니다."),
  CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 카테고리가 존재하지 않습니다."),
  EXIST_CATEGORY_NAME(HttpStatus.BAD_REQUEST, "해당 카테고리명이 이미 존재합니다."),
  SERIALIZING_CART_EXCEPTION(HttpStatus.UNPROCESSABLE_ENTITY, "Error serializing cart."),
  DESERIALIZING_CART_EXCEPTION(HttpStatus.UNPROCESSABLE_ENTITY, "Error deserializing cart."),
  KAKAO_LOGIN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 로그인 도중 실패했습니다."),
  KAKAO_MAP_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오맵 도중 실패했습니다."),
  JSON_PROCESS_ERROR(HttpStatus.BAD_REQUEST, "json 파싱 도중 실패했습니다.");

  private final HttpStatus httpStatus;
  private final String message;
}
