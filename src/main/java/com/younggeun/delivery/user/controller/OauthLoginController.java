package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.global.config.KakaoLoginConfig;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.user.domain.dto.UserDto;
import com.younggeun.delivery.user.domain.type.AuthType;
import com.younggeun.delivery.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("users/signin")
@AllArgsConstructor
public class OauthLoginController {

  private final UserService userService;
  private final TokenProvider tokenProvider;
  private final KakaoLoginConfig kakaoLoginConfig;

  @Operation(summary = "user 카카오 로그인 주소", description = "")
  @PostMapping("/oauth2/kakao")
  public ResponseEntity<?> kakaoLoginUri() {
    return ResponseEntity.ok(kakaoLoginConfig.getAuthorizeUri() + "?response_type=code&client_id=" + kakaoLoginConfig.getAdminKey() + "&redirect_uri=" + kakaoLoginConfig.getRedirectUri());
  }

  @Operation(summary = "user 카카오 로그인", description = "request")
  @GetMapping("/oauth2/kakao")
  public ResponseEntity<?> kakaoLogin(@RequestParam(required = false, name = "code") String code, @RequestParam(required = false, name = "error") String error) {
    if(error != null) return (ResponseEntity<?>) ResponseEntity.badRequest();
    var user = this.userService.ouath2Login(code);

    if(user.getPassword() != null && user.getAuthType() == AuthType.OAUTH) {
      var token = this.tokenProvider.generateToken(user.getEmail(), user.getRole());
      HttpHeaders headers = new HttpHeaders();
      headers.set("Authenticate", "Bearer " + token);
      return ResponseEntity.ok(token);
    } else {
      if(user.getCreatedAt() != null) {
        // 이미 존재하는 이메일 + provideId 가 등록되지 않은 경우 로그인 페이지로 이동
        // requestBody 로 userDto를 넘겨준다.
        return ResponseEntity.ok(new UserDto(user));
      } else {
        // 회원가입 페이지로 이동. requestBody로 userDto를 넘겨준다.
        return ResponseEntity.ok(new UserDto(user));
      }
    }
  }

}
