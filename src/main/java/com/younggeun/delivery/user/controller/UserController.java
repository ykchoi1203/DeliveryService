package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;
  private final TokenProvider tokenProvider;
  // 회원 가입
  @PostMapping("/signup")
  public ResponseEntity<?> signup(@RequestBody Auth.SignUp request) {
    var result = this.userService.register(request);
    return ResponseEntity.ok(result);
  }

  // 로그인
  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
    var user = this.userService.authenticate(request);
    var token = this.tokenProvider.generateToken(user.getUsername(), user.getRole());

    return ResponseEntity.ok(token);
  }


}
