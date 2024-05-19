package com.younggeun.delivery.store.controller;

import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.type.OrderType;
import com.younggeun.delivery.store.service.SearchService;
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
  private final SearchService searchService;

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

  @Operation(summary = "partner 상점 open", description = "storeId")
  @PutMapping("/partners/store/{storeId}/open")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> openStore(Authentication authentication, @PathVariable String storeId) {
    var result = storeService.changeOpened(authentication, storeId, true);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 close", description = "storeId")
  @PutMapping("/partners/store/{storeId}/close")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> closeStore(Authentication authentication, @PathVariable String storeId) {
    var result = storeService.changeOpened(authentication, storeId, false);
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

  @Operation(summary = "partner 상점 수정", description = "storeDto")
  @PutMapping("/partners/store/{storeId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStore(@PathVariable String storeId, Authentication authentication, @RequestBody StoreDto storeDto) {
    var result = storeService.updateStore(authentication, storeId, storeDto);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 삭제", description = "request")
  @PutMapping("/partners/store/{storeId}/delete")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> deleteStore(@PathVariable String storeId, Authentication authentication) {
    var result = storeService.deleteStore(authentication, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 상점 조회(검색 포함)", description = "")
  @GetMapping("/users/store")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> selectAllStore(Authentication authentication,
      @RequestParam(name = "category", required = false, defaultValue = "0") String categoryId,
      @RequestParam(name = "query", required = false, defaultValue = "") String query,
      @RequestParam(name = "orderType", required = false, defaultValue = "STAR") String orderType,
      @RequestParam(name = "order", required = false, defaultValue = "ASC") String asc,
      @RequestParam(name = "distance", required = false, defaultValue = "5000") String distance,
      @RequestParam(name = "page", required = false, defaultValue = "0") String page) {
    OrderType type = OrderType.valueOf(orderType);

    var result = searchService.findStores(authentication, type, distance,  Long.parseLong(categoryId), query, asc);
    return ResponseEntity.ok(result);
  }

}
