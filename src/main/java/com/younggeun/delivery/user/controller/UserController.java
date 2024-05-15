package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.user.domain.dto.DeliveryAddressDto;
import com.younggeun.delivery.user.domain.dto.UserDto;
import com.younggeun.delivery.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authenticate", "Bearer " + token);
    return ResponseEntity.ok(token);
  }

  @Operation(summary = "user 회원 정보 조회", description = "")
  @GetMapping
  public ResponseEntity<?> getUser(Authentication authentication) {
    var result = this.userService.getUser(authentication);

    return ResponseEntity.ok(result);
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

  @Operation(summary = "user 주소 list 조회", description = "authentication")
  @GetMapping("/address")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getAddress(Authentication authentication) {
    var result = this.userService.getAddress(authentication);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 주소 추가", description = "authentication, deliveryAddressDto")
  @PostMapping("/address")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> createAddress(Authentication authentication, @RequestBody
      DeliveryAddressDto deliveryAddressDto) {
    var result = this.userService.createAddress(authentication, deliveryAddressDto);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 주소 수정", description = "authentication, deliveryAddressDto")
  @PutMapping("/address/{addressId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> updateAddress(Authentication authentication, @RequestBody
  DeliveryAddressDto deliveryAddressDto, @PathVariable String addressId) {
    deliveryAddressDto.setAddressId(Long.valueOf(addressId));
    var result = this.userService.updateAddress(authentication, deliveryAddressDto);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 주소 삭제", description = "authentication")
  @PutMapping("/address/{addressId}/delete")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> deleteAddress(Authentication authentication, @PathVariable String addressId) {
    var result = this.userService.deleteAddress(authentication, Long.valueOf(addressId));

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 찜 가게 list 조회", description = "authentication")
  @GetMapping("/wish")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> getWishList(Authentication authentication) {
    var result = this.userService.getWishStore(authentication);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 찜 가게 추가", description = "authentication, storeId")
  @PostMapping("/wish/{storeId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> createWishStore(Authentication authentication, @PathVariable String storeId) {
    var result = this.userService.createWishStore(authentication, Long.valueOf(storeId));

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 찜 가게 삭제", description = "authentication")
  @DeleteMapping("/wish/{wishId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> deleteWishStore(Authentication authentication, @PathVariable String wishId) {
    var result = this.userService.deleteWishStore(authentication, Long.valueOf(wishId));

    return ResponseEntity.ok(result);
  }

}
