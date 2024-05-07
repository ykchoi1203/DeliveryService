package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StorePhoto;
import com.younggeun.delivery.store.domain.entity.StoreProfilePhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoDto {
  private String url;
  private String photoName;
  private Store store;

  public StorePhoto toStorePhotoEntity() {
    return StorePhoto.builder()
        .store(store)
        .url(url)
        .photoName(photoName)
        .build();
  }

  public StoreProfilePhoto toStoreProfilePhotoEntity() {
    return StoreProfilePhoto.builder()
        .store(store)
        .url(url)
        .photoName(photoName)
        .build();
  }

}
