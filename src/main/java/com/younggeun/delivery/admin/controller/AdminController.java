package com.younggeun.delivery.admin.controller;

import com.younggeun.delivery.admin.service.AdminService;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.global.security.TokenProvider;
import com.younggeun.delivery.store.domain.dto.CategoryDto;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
  private final AdminService adminService;
  private final TokenProvider tokenProvider;

  @Operation(summary = "admin 로그인", description = "request")
  @PostMapping("/signin")
  public ResponseEntity<?> signin(@RequestBody Auth.SignIn request) {
    var admin = adminService.authenticate(request);
    var token = this.tokenProvider.generateToken(admin.getEmail(), admin.getRole());

    return ResponseEntity.ok(token);
  }

  @GetMapping("category")
  public ResponseEntity<?> getCategory() {
    List<Category> category = adminService.getCategory();

    return ResponseEntity.ok(category);
  }

  @PostMapping("category")
  public ResponseEntity<?> createCategory(@RequestBody CategoryDto categoryDto) {
    var category = adminService.createCategory(categoryDto);

    return ResponseEntity.ok(category);
  }

  @PutMapping("category/{categoryId}")
  public ResponseEntity<?> updateCategory(@RequestBody CategoryDto categoryDto,
      @PathVariable String categoryId) {
    var category = adminService.updateCategory(categoryDto, Long.parseLong(categoryId));

    return ResponseEntity.ok(category);
  }

  @DeleteMapping("category/{categoryId}")
  public ResponseEntity<?> deleteCategory(@PathVariable String categoryId) {
    var result = adminService.deleteCategory(Long.parseLong(categoryId));

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "admin get store List", description = "")
  @GetMapping("store")
  public ResponseEntity<?> getStoreList(Pageable pageable) {
    Page<StoreDto> storeList = adminService.getStoreList(pageable);

    return ResponseEntity.ok(storeList);
  }

  @Operation(summary = "admin get store detail", description = "")
  @GetMapping("store/{storeId}")
  public ResponseEntity<?> getStoreDetail(@PathVariable String storeId) {
    var store = adminService.getStore(storeId);

    return ResponseEntity.ok(store);
  }

  @PutMapping("store/{storeId}/access")
  public ResponseEntity<?> accessStore(@PathVariable String storeId) {
    var store = adminService.changeStoreStatus(storeId, true);

    return ResponseEntity.ok(store);
  }

  @PutMapping("store/{storeId}/cancel")
  public ResponseEntity<?> accessCancelStore(@PathVariable String storeId) {
    var store = adminService.changeStoreStatus(storeId, false);

    return ResponseEntity.ok(store);
  }
}
