package com.younggeun.delivery.store.controller;

import com.younggeun.delivery.store.domain.dto.MenuCategoryDto;
import com.younggeun.delivery.store.domain.dto.MenuDto;
import com.younggeun.delivery.store.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MenuController {
  private final MenuService menuService;

  @Operation(summary = "partner 상점 메뉴 조회", description = "storeId")
  @GetMapping("/partners/{storeId}/menu")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> selectPartnerStoreMenu(Authentication authentication,
      @PathVariable String storeId) {
    var result = menuService.selectMenu(authentication, storeId);
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

  @Operation(summary = "partner 상점 메뉴 수정", description = "menuDto, storeId")
  @PutMapping("/partners/{storeId}/menu/{menuId}")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> updateStoreMenu(Authentication authentication, @RequestBody MenuDto menuDto,
      @PathVariable String storeId, @PathVariable String menuId) {
    menuDto.setMenuId(Long.parseLong(menuId));
    var result = menuService.updateStoreMenu(authentication, menuDto, storeId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 메뉴 삭제", description = "menuDto, storeId")
  @PutMapping("/partners/{storeId}/menu/{menuId}/delete")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> deleteStoreMenu(Authentication authentication,
      @PathVariable String storeId, @PathVariable String menuId) {
    var result = menuService.deleteStoreMenu(authentication, Long.parseLong(menuId), storeId);
    return ResponseEntity.ok(result);
  }

}
