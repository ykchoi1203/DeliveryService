package com.younggeun.delivery.store.controller;

import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StoreController {
  private final StoreService storeService;

  @Value("${store.photo.baseLocalPath}")
  private String photoBaseLocalPath;

  @Value("${store.photo.baseUrlPath}")
  private String photoBaseUrlPath;

  @Value("${store.profile.baseLocalPath}")
  private String profileBaseLocalPath;

  @Value("${store.profile.baseUrlPath}")
  private String profileBaseUrlPath;

  @Operation(summary = "partner 상점 조회", description = "Pageable")
  @GetMapping("/partners/store")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> selectPartnerStore(Authentication authentication, Pageable pageable) {
    var result = storeService.selectPartnerStore(authentication, pageable);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 추가", description = "request")
  @PostMapping("/partners/store")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStore(@RequestBody StoreDto request, Authentication authentication) {
    var result = storeService.createStore(authentication, request);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 사진 추가", description = "request")
  @PostMapping("/partners/store/photo/{storeId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStorePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId) {
    var result = storeService.createStorePhoto(authentication, file, Long.parseLong(storeId), photoBaseLocalPath, photoBaseUrlPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 사진 변경", description = "request")
  @PutMapping("/partners/store/photo/{storeId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStorePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId) {
    var result = storeService.updateStorePhoto(authentication, file, Long.parseLong(storeId), photoBaseLocalPath, photoBaseUrlPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 프로필 사진 추가", description = "request")
  @PostMapping("/partners/store/photo/{storeId}/profile")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStoreProfilePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId) {

    var result = storeService.createStoreProfilePhoto(authentication, file, Long.parseLong(storeId), profileBaseLocalPath, profileBaseUrlPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 프로필 사진 변경", description = "request")
  @PutMapping("/partners/store/photo/{storeId}/profile")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStoreProfilePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId) {

    var result = storeService.updateStoreProfilePhoto(authentication, file, Long.parseLong(storeId), profileBaseLocalPath, profileBaseUrlPath);
    return ResponseEntity.ok(result);
  }

}
