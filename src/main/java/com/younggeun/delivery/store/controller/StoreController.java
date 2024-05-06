package com.younggeun.delivery.store.controller;

import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

    PhotoDto storePhotoDto = new PhotoDto();
    String saveFilename = "";
    String urlFilename = "";

    if (file != null) {
      String originalFilename = file.getOriginalFilename();

      String[] arrFilename = getNewSaveFile(photoBaseLocalPath, photoBaseUrlPath, originalFilename);

      saveFilename = arrFilename[0];
      urlFilename = arrFilename[1];

      try {
        File newFile = new File(saveFilename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
      } catch (IOException e) {
        log.warn(e.getMessage());
      }
    }

    storePhotoDto.setUrl(urlFilename);
    storePhotoDto.setPhotoName(saveFilename);

    var result = storeService.createStorePhoto(authentication, storePhotoDto, Long.parseLong(storeId));
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "partner 상점 프로필 사진 추가", description = "request")
  @PostMapping("/partners/store/photo/{storeId}/profile")
  @PreAuthorize("hasRole('PARTNER')")
  public ResponseEntity<?> createStoreProfilePhoto(Authentication authentication, @RequestParam("file") MultipartFile file,
      @PathVariable String storeId) {

    PhotoDto storeProfilePhotoDto = new PhotoDto();
    String saveFilename = "";
    String urlFilename = "";

    if (file != null) {
      String originalFilename = file.getOriginalFilename();

      String[] arrFilename = getNewSaveFile(profileBaseLocalPath, profileBaseUrlPath, originalFilename);

      saveFilename = arrFilename[0];
      urlFilename = arrFilename[1];

      try {
        File newFile = new File(saveFilename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
      } catch (IOException e) {
        log.warn(e.getMessage());
      }
    }

    storeProfilePhotoDto.setUrl(urlFilename);
    storeProfilePhotoDto.setPhotoName(saveFilename);

    var result = storeService.createStoreProfilePhoto(authentication, storeProfilePhotoDto, Long.parseLong(storeId));
    return ResponseEntity.ok(result);
  }

  private String[] getNewSaveFile(String baseLocalPath, String baseUrlPath, String originalFilename) {

    LocalDate now = LocalDate.now();

    String[] dirs = {
        String.format("%s/%d/", baseLocalPath,now.getYear()),
        String.format("%s/%d/%02d/", baseLocalPath, now.getYear(),now.getMonthValue()),
        String.format("%s/%d/%02d/%02d/", baseLocalPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth())};

    String urlDir = String.format("%s/%d/%02d/%02d/", baseUrlPath, now.getYear(), now.getMonthValue(), now.getDayOfMonth());

    for(String dir : dirs) {
      File file = new File(dir);
      if (!file.isDirectory()) {
        file.mkdir();
      }
    }

    String fileExtension = "";
    if (originalFilename != null) {
      int dotPos = originalFilename.lastIndexOf(".");
      if (dotPos > -1) {
        fileExtension = originalFilename.substring(dotPos + 1);
      }
    }

    String uuid = UUID.randomUUID().toString().replaceAll("-", "");
    String newFilename = String.format("%s%s", dirs[2], uuid);
    String newUrlFilename = String.format("%s%s", urlDir, uuid);
    if (fileExtension.length() > 0) {
      newFilename += "." + fileExtension;
      newUrlFilename += "." + fileExtension;
    }

    return new String[]{newFilename, newUrlFilename};
  }
}
