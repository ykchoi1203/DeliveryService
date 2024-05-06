package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.user.domain.dto.UserDto;
import com.younggeun.delivery.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final TokenProvider tokenProvider;

  // 회원 가입
  @Operation(summary = "user 회원가입", description = "request")
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
    var result = this.userService.register(request);
    return ResponseEntity.ok(result);
  }

  // 로그인
  @Operation(summary = "user 로그인", description = "request")
  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
    var user = this.userService.authenticate(request);
    var token = this.tokenProvider.generateToken(user.getEmail(), user.getRole());

    return ResponseEntity.ok(token);
  }

  // 회원 정보 수정 ( 비밀번호, 닉네임, 전화번호, 이름 )
  @Operation(summary = "user 회원 정보 수정", description = "request")
  @PutMapping
  public ResponseEntity<?> update(@RequestBody UserDto request, Authentication authentication) {
    var result = this.userService.updateUser(request, authentication);

    return ResponseEntity.ok(result);
  }

  // 회원 탈퇴
  @Operation(summary = "user 회원 탈퇴", description = "request")
  @PutMapping("/delete")
  public ResponseEntity<?> delete(Authentication authentication) {
    var result = this.userService.deleteUser(authentication);

    return ResponseEntity.ok(result);
  }


}
