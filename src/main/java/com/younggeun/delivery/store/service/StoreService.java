package com.younggeun.delivery.store.service;

import static com.younggeun.delivery.global.exception.type.CommonErrorCode.KAKAO_MAP_ERROR;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.EXIST_PHOTO_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_PARTNER_STORE;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_CATEGORY_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.config.KakaoMapConfig;
import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.partner.domain.PartnerRepository;
import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.CategoryRepository;
import com.younggeun.delivery.store.domain.StorePhotoRepository;
import com.younggeun.delivery.store.domain.StoreProfilePhotoRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.documents.StoreDocument;
import com.younggeun.delivery.store.domain.documents.repository.StoreDocumentRepository;
import com.younggeun.delivery.store.domain.dto.KakaoMapResponse;
import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.entity.Category;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StorePhoto;
import com.younggeun.delivery.store.domain.entity.StoreProfilePhoto;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@AllArgsConstructor
public class StoreService {
  private final StoreRepository storeRepository;
  private final StorePhotoRepository storePhotoRepository;
  private final StoreProfilePhotoRepository storeProfilePhotoRepository;
  private final PartnerRepository partnerRepository;
  private final CategoryRepository categoryRepository;
  private final RestTemplate restTemplate;
  private final KakaoMapConfig kakaoMapConfig;
  private final StoreDocumentRepository storeDocumentRepository;

  public Page<StoreDto> selectPartnerStore(Authentication authentication, Pageable pageable) {
    Partner partner = getPartner(authentication);

    return storeToDto(storeRepository.findAllByPartner(partner, pageable));
  }

  @Transactional
  public Store createStore(Authentication authentication, StoreDto request) {
    Partner partner = getPartner(authentication);
    Category category = categoryRepository.findById(request.getCategoryId()).orElseThrow(RuntimeException::new);

    extracted(request);
    Store store = storeRepository.save(Store.builder()
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
        .accessStatus(true) // 추후 admin 에서 허가를 하는 기능을 만든다면 false로 바꿀 예정.
        .category(category)
        .partner(partner).build());

    // 추후 admin 에서 허가를 하는 기능을 만든다면 그 메서드에서 저장할 예정.
    storeDocumentRepository.save(new StoreDocument(store));

    return store;
  }

  private void extracted(StoreDto request) {

    try {
      HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(this.getHeader());

      UriComponents uriComponents = UriComponentsBuilder.fromUriString(kakaoMapConfig.getMapUrl())
          .queryParam("analyze_type", "similar")
          .queryParam("page", "1")
          .queryParam("size", "10")
          .queryParam("query", request.getAddress1() + " " + request.getAddress2() + " " + request.getAddress3())
          .encode(StandardCharsets.UTF_8) // UTF-8로 인코딩
          .build();

      URI targetUrl = uriComponents.toUri();
      ResponseEntity<Map> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, Map.class);
      KakaoMapResponse kakaoMapResponse = new KakaoMapResponse((ArrayList)responseEntity.getBody().get("documents"));
      request.setLatitude(Double.parseDouble(kakaoMapResponse.getY()));
      request.setLongitude(Double.parseDouble(kakaoMapResponse.getX()));

    } catch (HttpClientErrorException e) {
      throw new RestApiException(KAKAO_MAP_ERROR);
    }
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

  private Page<StoreDto> storeToDto(Page<Store> list) {
    List<Long> storeIds = list.stream().map(Store::getStoreId).toList();

    Map<Long, PhotoDto> storePhotoList = storePhotoRepository.findAllByStoreStoreIdIn(storeIds).stream().map(PhotoDto::new).collect(
        Collectors.toMap(PhotoDto::getStoreId, photoDto -> photoDto));
    Map<Long, PhotoDto> storeProfilePhotoList = storeProfilePhotoRepository.findAllByStoreStoreIdIn(storeIds).stream().map(PhotoDto::new).collect(Collectors.toMap(PhotoDto::getStoreId, photoDto -> photoDto));
    Page<StoreDto> storeDtoPage = list.map(StoreDto::new);

    storeDtoPage.forEach(item -> {
      Long storeId = item.getStoreId();
      PhotoDto storePhoto = storePhotoList.get(storeId);
      PhotoDto storeProfilePhoto = storeProfilePhotoList.get(storeId);
      item.setStorePhoto(storePhoto);
      item.setStoreProfilePhoto(storeProfilePhoto);
    });

    return storeDtoPage;
  }

  public StoreDto changeOpened(Authentication authentication, String storeId, boolean isOpened) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(Long.valueOf(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(partner.getPartnerId() != store.getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    store.setOpened(isOpened);

    return new StoreDto(storeRepository.save(store));

  }

  private HttpHeaders getHeader() {
    HttpHeaders httpHeaders = new HttpHeaders();
    String auth = "KakaoAK " + kakaoMapConfig.getAdminKey();

    httpHeaders.set("Authorization", auth);

    return httpHeaders;
  }

  @Transactional
  public boolean deleteStore(Authentication authentication, String storeId) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(Long.valueOf(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(partner.getPartnerId() != store.getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }
    store.setDeletedAt(LocalDateTime.now());

    storeRepository.save(store);
    storeDocumentRepository.deleteById(Long.parseLong(storeId));

    return true;
  }

  @Transactional
  public StoreDto updateStore(Authentication authentication, String storeId, StoreDto storeDto) {
    Partner partner = getPartner(authentication);
    Store store = storeRepository.findById(Long.valueOf(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(partner.getPartnerId() != store.getPartner().getPartnerId()) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }

    if(storeDto.getCategoryId() != store.getCategory().getCategoryId()) {
      store.setCategory(categoryRepository.findById(storeDto.getCategoryId()).orElseThrow(() -> new RestApiException(STORE_CATEGORY_NOT_FOUND)));
    }

    extracted(storeDto);
    store.update(storeDto);

    store = storeRepository.save(store);

    storeDocumentRepository.save(new StoreDocument(store));

    return new StoreDto(store);
  }

}
