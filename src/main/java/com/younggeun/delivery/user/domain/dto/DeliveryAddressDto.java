package com.younggeun.delivery.user.domain.dto;

import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DeliveryAddressDto {
  private Long addressId;
  private String name;

  private String address1;
  private String address2;
  private String address3;

  private double latitude;
  private double longitude;

  private boolean defaultAddressStatus;

  public DeliveryAddressDto(DeliveryAddress deliveryAddress) {
    this.name = deliveryAddress.getName();
    this.addressId = deliveryAddress.getAddressId();
    this.address1 = deliveryAddress.getAddress1();
    this.address2 = deliveryAddress.getAddress2();
    this.address3 = deliveryAddress.getAddress3();
    this.latitude = deliveryAddress.getLatitude();
    this.longitude = deliveryAddress.getLongitude();
    this.defaultAddressStatus = deliveryAddress.isDefaultAddressStatus();
  }

  public DeliveryAddress toEntity(User user) {
    return DeliveryAddress.builder()
        .name(name)
        .address1(address1)
        .address2(address2)
        .address3(address3)
        .latitude(latitude)
        .longitude(longitude)
        .user(user)
        .defaultAddressStatus(true)
        .build();
  }

  public DeliveryAddress toEntity(DeliveryAddress deliveryAddress) {
    return DeliveryAddress.builder()
        .name(name)
        .addressId(addressId)
        .address1(address1)
        .address2(address2)
        .address3(address3)
        .latitude(latitude)
        .longitude(longitude)
        .user(deliveryAddress.getUser())
        .defaultAddressStatus(defaultAddressStatus)
        .build();
  }
}
