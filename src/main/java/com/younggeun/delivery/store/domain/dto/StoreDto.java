package com.younggeun.delivery.store.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.younggeun.delivery.store.domain.entity.Store;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreDto {
  private Long storeId;
  private String storeName;
  private String phone;
  private String address1;
  private String address2;
  private String address3;
  private double latitude;
  private double longitude;
  private String businessNumber;

  private LocalDateTime lastOpenTime;
  private LocalDateTime lastCloseTime;

  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime openTime;
  @JsonFormat(pattern = "HH:mm:ss")
  private LocalTime endTime;

  private Long totalStars;
  private Long totalReviews;
  private int leastOrderCost;
  private int deliveryCost;
  private String originNotation;
  private boolean accessStatus;
  private Long partnerId;
  private Long categoryId;
  private boolean isOpened;

  private PhotoDto storePhoto;
  private PhotoDto storeProfilePhoto;

  public StoreDto(Store store) {
    storeId = store.getStoreId();
    storeName = store.getStoreName();
    phone = store.getPhone();
    address1 = store.getAddress1();
    address2 = store.getAddress2();
    address3 = store.getAddress3();
    latitude = store.getLatitude();
    longitude = store.getLongitude();
    businessNumber = store.getBusinessNumber();
    openTime = store.getOpenTime();
    endTime = store.getEndTime();
    totalReviews = store.getTotalReviews();
    totalStars = store.getTotalStars();
    leastOrderCost = store.getLeastOrderCost();
    deliveryCost = store.getDeliveryCost();
    originNotation = store.getOriginNotation();
    accessStatus = store.isAccessStatus();
    categoryId = store.getCategory().getCategoryId();
    partnerId = store.getPartner().getPartnerId();
    isOpened = store.isOpened();
    lastOpenTime = store.getLastOpenTime();
    lastCloseTime = store.getLastCloseTime();
  }

  public Store toEntity() {
    return Store.builder()
        .storeName(storeName)
        .phone(phone)
        .address1(address1)
        .address2(address2)
        .address3(address3)
        .latitude(latitude)
        .longitude(longitude)
        .businessNumber(businessNumber)
        .openTime(openTime)
        .endTime(endTime)
        .isOpened(isOpened)
        .totalStars(totalStars)
        .totalReviews(totalReviews)
        .leastOrderCost(leastOrderCost)
        .deliveryCost(deliveryCost)
        .originNotation(originNotation)
        .accessStatus(accessStatus)
        .build();
  }

}
