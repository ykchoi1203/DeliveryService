package com.younggeun.delivery.store.service;

import static com.younggeun.delivery.global.exception.type.CommonErrorCode.FILE_SAVE_ERROR;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_PARTNER_STORE;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.partner.domain.PartnerRepository;
import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.CategoryRepository;
import com.younggeun.delivery.store.domain.StorePhotoRepository;
import com.younggeun.delivery.store.domain.StoreProfilePhotoRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.entity.Category;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StorePhoto;
import com.younggeun.delivery.store.domain.entity.StoreProfilePhoto;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {
  private final StoreRepository storeRepository;
  private final StorePhotoRepository storePhotoRepository;
  private final StoreProfilePhotoRepository storeProfilePhotoRepository;
  private final PartnerRepository partnerRepository;
  private final CategoryRepository categoryRepository;

  public Page<StoreDto> selectPartnerStore(Authentication authentication, Pageable pageable) {
    Partner partner = getPartner(authentication);
    Page<Store> stores = storeRepository.findAllByPartner(partner, pageable);

    return stores.map(store -> {
      StoreDto storeDto = new StoreDto(store);

      StorePhoto storePhoto = storePhotoRepository.findByStore(store)
          .orElse(null);

      storeDto.setStorePhoto(storePhoto);

      StoreProfilePhoto storeProfilePhoto = storeProfilePhotoRepository.findByStore(store).orElse(null);

      storeDto.setStoreProfilePhoto(storeProfilePhoto);

      return storeDto;
    });
  }

  @Transactional
  public Store createStore(Authentication authentication, StoreDto request) {
    Partner partner = getPartner(authentication);
    Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(RuntimeException::new);

    return storeRepository.save(Store.builder()
        .storeName(request.getStoreName())
        .phone(request.getPhone())
        .address1(request.getAddress1())
        .address2(request.getAddress2())
        .address3(request.getAddress3())
        .latitude(request.getLatitude())
        .longitude(request.getLongitude())
        .businessNumber(request.getBusinessNumber())
        .openTime(request.getOpenTime())
        .endTime(request.getEndTime())
        .totalReviews(0L)
        .totalStars(0L)
        .leastOrderCost(request.getLeastOrderCost())
        .deliveryCost(request.getDeliveryCost())
        .originNotation(request.getOriginNotation())
        .accessStatus(false)
        .category(category)
        .partner(partner).build());
  }

  public StorePhoto createStorePhoto(Authentication authentication, MultipartFile file, Long storeId,
      String photoBaseLocalPath, String photoBaseUrlPath) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(storeId).orElseThrow();

    if(!Objects.equals(store.getPartner().getPartnerId(), partner.getPartnerId())) {
      throw new RuntimeException();
    }

    PhotoDto storePhotoDto = savePhotos(file, photoBaseLocalPath, photoBaseUrlPath);

    storePhotoDto.setStore(store);

    return storePhotoRepository.save(storePhotoDto.toStorePhotoEntity());
  }

  public StoreProfilePhoto createStoreProfilePhoto(Authentication authentication, MultipartFile file, long storeId,
      String profileBaseLocalPath, String profileBaseUrlPath) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(storeId).orElseThrow();

    if(!Objects.equals(store.getPartner().getPartnerId(), partner.getPartnerId())) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    PhotoDto storeProfilePhotoDto = savePhotos(file, profileBaseLocalPath, profileBaseUrlPath);

    storeProfilePhotoDto.setStore(store);

    return storeProfilePhotoRepository.save(storeProfilePhotoDto.toStoreProfilePhotoEntity());
  }

  private PhotoDto savePhotos(MultipartFile file, String localPath, String urlPath) {
    PhotoDto storePhotoDto = new PhotoDto();
    String saveFilename = "";
    String urlFilename = "";

    if (file != null) {
      String originalFilename = file.getOriginalFilename();

      String[] arrFilename = getNewSaveFile(localPath, urlPath, originalFilename);

      saveFilename = arrFilename[0];
      urlFilename = arrFilename[1];

      try {
        File newFile = new File(saveFilename);
        FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(newFile));
      } catch (IOException e) {
        log.warn(e.getMessage());
        throw new RestApiException(FILE_SAVE_ERROR);
      }
    }

    storePhotoDto.setUrl(urlFilename);
    storePhotoDto.setPhotoName(saveFilename);

    return storePhotoDto;
  }

  private Partner getPartner(Authentication authentication) {
    return partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
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
