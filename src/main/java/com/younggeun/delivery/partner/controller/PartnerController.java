package com.younggeun.delivery.partner.controller;

import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.partner.domain.dto.PartnerDto;
import com.younggeun.delivery.partner.service.PartnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/partner")
@RequiredArgsConstructor
public class PartnerController {
  private final PartnerService partnerService;
  private final TokenProvider tokenProvider;
  // 회원 가입
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
    var result = this.partnerService.register(request);
    return ResponseEntity.ok(result);
  }

  // 로그인
  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
    var partner = this.partnerService.authenticate(request);
    var token = this.tokenProvider.generateToken(partner.getEmail(), partner.getRole());

    return ResponseEntity.ok(token);
  }

  // 파트너 정보 수정 ( 비밀번호, 주소, 전화번호, 이름 )
  @PutMapping("/update/{partnerId}")
  public ResponseEntity<?> update(@RequestBody PartnerDto request, Authentication authentication,
      @PathVariable String partnerId) {
    var result = this.partnerService.updatePartner(request, authentication);

    return ResponseEntity.ok(result);
  }

  // 파트너 탈퇴
  @PutMapping("/delete")
  public ResponseEntity<?> delete(Authentication authentication) {
    var result = this.partnerService.deletePartner(authentication);

    return ResponseEntity.ok(result);
  }

}
