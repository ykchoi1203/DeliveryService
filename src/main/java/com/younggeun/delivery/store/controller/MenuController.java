package com.younggeun.delivery.store.controller;

import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.store.domain.dto.AdditionalMenuDto;
import com.younggeun.delivery.store.domain.dto.MenuCategoryDto;
import com.younggeun.delivery.store.domain.dto.MenuDto;
import com.younggeun.delivery.store.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
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
public class MenuController {
  private final MenuService menuService;

  @Value("${menu.photo.baseLocalPath}")
  private String menuBaseLocalPath;

  @Value("${menu.photo.baseUrlPath}")
  private String menuBaseUrlPath;

  @Operation(summary = "partner 상점 메뉴 전체 조회", description = "storeId")
  @GetMapping("/partners/{storeId}/menu")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> selectPartnerStoreMenu(Authentication authentication,
      @PathVariable String storeId) {
    var result = menuService.selectMenu(authentication, storeId, RoleType.ROLE_PARTNER);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 카테고리 조회", description = "storeId")
  @GetMapping("/partners/{storeId}/category")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> selectPartnerStoreMenuCategory(Authentication authentication,
      @PathVariable String storeId) {
    var result = menuService.selectMenuCategory(authentication, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 카테고리 추가", description = "menuCategoryDto, storeId")
  @PostMapping("/partners/{storeId}/category")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStoreCategory(Authentication authentication, @RequestBody MenuCategoryDto menuCategoryDto,
      @PathVariable String storeId) {
    var result = menuService.createStoreCategory(authentication, menuCategoryDto, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 추가", description = "menuDto, storeId")
  @PostMapping("/partners/{storeId}/menu")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStoreMenu(Authentication authentication, @RequestBody MenuDto menuDto,
      @PathVariable String storeId) {
    var result = menuService.createStoreMenu(authentication, menuDto, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 상세 조회", description = "menuDto, storeId")
  @GetMapping("/partners/{storeId}/menu/{menuId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> selectMenuDetails(Authentication authentication,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.selectMenuDetails(authentication, storeId, menuId, RoleType.ROLE_PARTNER);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 수정", description = "menuDto, storeId")
  @PutMapping("/partners/{storeId}/menu/{menuId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStoreMenu(Authentication authentication, @RequestBody MenuDto menuDto,
      @PathVariable String storeId, @PathVariable String menuId) {
    menuDto.setMenuId(Long.parseLong(menuId));
    var result = menuService.updateStoreMenu(authentication, menuDto, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 sold out", description = "menuDto, storeId")
  @PutMapping("/partners/{storeId}/menu/{menuId}/soldOut")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateSoldOutStoreMenu(Authentication authentication,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.updateSoldOutStoreMenu(authentication, storeId, menuId, true);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 재판매", description = "menuDto, storeId")
  @PutMapping("/partners/{storeId}/menu/{menuId}/resale")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateResaleStoreMenu(Authentication authentication,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.updateSoldOutStoreMenu(authentication, storeId, menuId, false);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 카테고리 수정", description = "menuCategoryDto, storeId")
  @PutMapping("/partners/{storeId}/category/{categoryId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStoreMenuCategory(Authentication authentication, @RequestBody MenuCategoryDto menuCategoryDto,
      @PathVariable String storeId, @PathVariable String categoryId) {
    var result = menuService.updateStoreMenuCategory(authentication, menuCategoryDto, storeId, categoryId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 삭제", description = "menuId, storeId")
  @PutMapping("/partners/{storeId}/menu/{menuId}/delete")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> deleteStoreMenu(Authentication authentication,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.deleteStoreMenu(authentication, storeId, Long.parseLong(menuId));
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 카테고리 삭제", description = "storeId, menuCategoryId")
  @DeleteMapping("/partners/{storeId}/category/{categoryId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> deleteStoreMenuCategory(Authentication authentication,
      @PathVariable String storeId, @PathVariable String categoryId) {
    var result = menuService.deleteStoreMenuCategory(authentication,  storeId, Long.parseLong(categoryId));
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "menu 사진 추가", description = "request")
  @PostMapping("/partners/{storeId}/menu/{menuId}/photo")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStorePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.createMenuPhoto(authentication, file, storeId, Long.parseLong(menuId), menuBaseLocalPath, menuBaseUrlPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 메뉴 사진 변경", description = "request")
  @PutMapping("/partners/{storeId}/menu/{menuId}/photo")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStorePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.updateMenuPhoto(authentication, file, storeId, Long.parseLong(menuId), menuBaseLocalPath, menuBaseUrlPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 추가 메뉴 추가", description = "menuDto, storeId")
  @PostMapping("/partners/{storeId}/addition/{menuId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createAdditionalMenu(Authentication authentication, @RequestBody AdditionalMenuDto additionalMenuDto,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.createAdditionalMenu(authentication, additionalMenuDto, storeId, menuId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 추가 메뉴 수정", description = "menuDto, storeId, additionalId")
  @PutMapping("/partners/{storeId}/addition/{additionalId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateAdditionalMenu(Authentication authentication, @RequestBody AdditionalMenuDto additionalMenuDto,
      @PathVariable String storeId, @PathVariable String additionalId) {
    additionalMenuDto.setAdditionalMenuId(Long.parseLong(additionalId));
    var result = menuService.updateAdditionalMenu(authentication, additionalMenuDto, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 추가 메뉴 삭제", description = "storeId, additionalId")
  @PutMapping("/partners/{storeId}/addition/{additionalId}/delete")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> deleteAdditionalMenu(Authentication authentication,
      @PathVariable String storeId, @PathVariable String additionalId) {
    var result = menuService.deleteAdditionalMenu(authentication,  storeId, Long.parseLong(additionalId));
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 추가 메뉴 sold out", description = "menuDto, storeId, additionalId")
  @PutMapping("/partners/{storeId}/addition/{additionalId}/soldOut")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateSoldOutAdditionalMenu(Authentication authentication
      ,@PathVariable String storeId, @PathVariable String additionalId) {
    var result = menuService.updateSoldOutAdditionalMenu(authentication, additionalId, storeId, true);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 soldout 된 추가 메뉴 재판매", description = "menuDto, storeId, additionalId")
  @PutMapping("/partners/{storeId}/addition/{additionalId}/resale")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateResaleAdditionalMenu(Authentication authentication
      ,@PathVariable String storeId, @PathVariable String additionalId) {
    var result = menuService.updateSoldOutAdditionalMenu(authentication, additionalId, storeId, false);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 상점 메뉴 조회", description = "storeId")
  @GetMapping("/users/{storeId}/menu")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> selectUserStoreMenu(Authentication authentication,
      @PathVariable String storeId) {
    var result = menuService.selectMenu(authentication, storeId, RoleType.ROLE_USER);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "user 상점 메뉴 상세 조회", description = "storeId")
  @GetMapping("/users/{storeId}/menu/{menuId}")
  @PreAuthorize("hasRole('USER')")
  public ResponseEntity<?> selectUserMenuDetails(Authentication authentication,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.selectMenuDetails(authentication, storeId, menuId, RoleType.ROLE_USER);
    return ResponseEntity.ok(result);
  }

}
