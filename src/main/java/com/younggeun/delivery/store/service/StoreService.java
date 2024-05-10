package com.younggeun.delivery.store.service;

import static com.younggeun.delivery.global.exception.type.StoreErrorCode.EXIST_PHOTO_EXCEPTION;
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
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

  @Transactional
  public StorePhoto createStorePhoto(Authentication authentication, MultipartFile file, Long storeId,
      String photoBaseLocalPath, String photoBaseUrlPath) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(storeId).orElseThrow();

    if(!Objects.equals(store.getPartner().getPartnerId(), partner.getPartnerId())) {
      throw new RuntimeException();
    }

    if(storePhotoRepository.existsByStore(store)) {
      throw new RestApiException(EXIST_PHOTO_EXCEPTION);
    }

    PhotoDto storePhotoDto = new PhotoDto();
    storePhotoDto.savePhotos(file, photoBaseLocalPath, photoBaseUrlPath);

    storePhotoDto.setStore(store);

    return storePhotoRepository.save(storePhotoDto.toStorePhotoEntity());
  }

  @Transactional
  public StorePhoto updateStorePhoto(Authentication authentication, MultipartFile file, Long storeId, String photoBaseLocalPath, String photoBaseUrlPath) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(storeId).orElseThrow();

    if(!Objects.equals(store.getPartner().getPartnerId(), partner.getPartnerId())) {
      throw new RuntimeException();
    }

    storePhotoRepository.findByStore(store).ifPresent(photo -> {
      photo.setDeletedAt(LocalDateTime.now());
      storePhotoRepository.save(photo);
    });

    PhotoDto storePhotoDto = new PhotoDto();
    storePhotoDto.savePhotos(file, photoBaseLocalPath, photoBaseUrlPath);

    storePhotoDto.setStore(store);

    return storePhotoRepository.save(storePhotoDto.toStorePhotoEntity());
  }

  @Transactional
  public StoreProfilePhoto createStoreProfilePhoto(Authentication authentication, MultipartFile file, long storeId,
      String profileBaseLocalPath, String profileBaseUrlPath) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(storeId).orElseThrow();

    if(!Objects.equals(store.getPartner().getPartnerId(), partner.getPartnerId())) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    if(storeProfilePhotoRepository.existsByStore(store)) {
      throw new RestApiException(EXIST_PHOTO_EXCEPTION);
    }

    PhotoDto storeProfilePhotoDto = new PhotoDto();
    storeProfilePhotoDto.savePhotos(file, profileBaseLocalPath, profileBaseUrlPath);

    storeProfilePhotoDto.setStore(store);

    return storeProfilePhotoRepository.save(storeProfilePhotoDto.toStoreProfilePhotoEntity());
  }

  @Transactional
  public StoreProfilePhoto updateStoreProfilePhoto(Authentication authentication, MultipartFile file, Long storeId, String profileBaseLocalPath, String profileBaseUrlPath) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(storeId).orElseThrow();

    if(!Objects.equals(store.getPartner().getPartnerId(), partner.getPartnerId())) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    storeProfilePhotoRepository.findByStore(store).ifPresent(photo -> {
      photo.setDeletedAt(LocalDateTime.now());
      storeProfilePhotoRepository.save(photo);
    });

    PhotoDto storeProfilePhotoDto = new PhotoDto();
    storeProfilePhotoDto.savePhotos(file, profileBaseLocalPath, profileBaseUrlPath);

    storeProfilePhotoDto.setStore(store);

    return storeProfilePhotoRepository.save(storeProfilePhotoDto.toStoreProfilePhotoEntity());
  }

  private Partner getPartner(Authentication authentication) {
    return partnerRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
  }

}
