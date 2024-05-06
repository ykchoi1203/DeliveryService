package com.younggeun.delivery.partner.controller;

import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.partner.domain.dto.PartnerDto;
import com.younggeun.delivery.partner.service.PartnerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/partners")
@RequiredArgsConstructor
public class PartnerController {
  private final PartnerService partnerService;
  private final TokenProvider tokenProvider;
  // 회원 가입
  @Operation(summary = "partner 회원가입", description = "request")
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
    var result = this.partnerService.register(request);
    return ResponseEntity.ok(result);
  }

  // 로그인
  @Operation(summary = "partner 로그인", description = "request")
  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
    var partner = this.partnerService.authenticate(request);
    var token = this.tokenProvider.generateToken(partner.getEmail(), partner.getRole());

    return ResponseEntity.ok(token);
  }

  // 파트너 정보 조회
  @Operation(summary = "partner 정보 조회", description = "")
  @GetMapping
  public ResponseEntity<?> getPartner(Authentication authentication) {
    var result = this.partnerService.getPartner(authentication);

    return ResponseEntity.ok(result);
  }

  // 파트너 정보 수정 ( 비밀번호, 주소, 전화번호, 이름 )
  @Operation(summary = "partner 정보 수정", description = "request")
  @PutMapping
  public ResponseEntity<?> update(@RequestBody PartnerDto request, Authentication authentication) {
    var result = this.partnerService.updatePartner(request, authentication);

    return ResponseEntity.ok(result);
  }

  // 파트너 탈퇴
  @Operation(summary = "partner 탈퇴", description = "request")
  @PutMapping("/delete")
  public ResponseEntity<?> delete(Authentication authentication) {
    var result = this.partnerService.deletePartner(authentication);

    return ResponseEntity.ok(result);
  }

}
