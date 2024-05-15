package com.younggeun.delivery.user.controller;

import static com.younggeun.delivery.global.exception.type.PayErrorCode.KAKAOPAY_FAILED;
import static com.younggeun.delivery.global.exception.type.PayErrorCode.KAKOPAY_CANCELED;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.user.domain.dto.KakaoCancelResponse;
import com.younggeun.delivery.user.domain.dto.KakaoReadyResponse;
import com.younggeun.delivery.user.domain.dto.KakaoRequestDto;
import com.younggeun.delivery.user.service.KakaoPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("users/payment/kakao")
@RequiredArgsConstructor
public class KakaoPayController {

  private final KakaoPayService kakaoPayService;

  private KakaoRequestDto kakaoRequestDto;

  /**
   * 결제요청
   */
  @PostMapping("/ready")
  public String readyToKakaoPay(Authentication authentication) {
    KakaoReadyResponse kakaoReadyResponse = kakaoPayService.kakaoPayReady(authentication);
    kakaoRequestDto = kakaoReadyResponse.getRequestDto();
    return "redirect:" + kakaoReadyResponse.getNext_redirect_pc_url();
  }

  /**
   * 결제 성공
   */
  @GetMapping("/success")
  public String afterPayRequest(HttpServletRequest request, Authentication authentication, @RequestParam("pg_token") String pgToken) {
    kakaoRequestDto.setPgToken(pgToken);

    return kakaoPayService.approveResponse(kakaoRequestDto, authentication, request.getHeader("Authorization"));
  }

  /**
   * 결제 진행 중 취소
   */
  @GetMapping("/cancel")
  public void cancel() {

    throw new RestApiException(KAKOPAY_CANCELED);
  }

  /**
   * 결제 실패
   */
  @GetMapping("/fail")
  public void fail() {

    throw new RestApiException(KAKAOPAY_FAILED);
  }

  /**
   * 환불
   */
  @PostMapping("/refund")
  public ResponseEntity refund(Authentication authentication) {

    KakaoCancelResponse kakaoCancelResponse = kakaoPayService.kakaoCancel(authentication);

    return new ResponseEntity<>(kakaoCancelResponse, HttpStatus.OK);
  }
}