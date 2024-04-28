package com.younggeun.delivery.partner.domain.dto;

import jakarta.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
public class PartnerDto {
  private String password;
  private String partnerName;
  private String phoneNumber;
  private String address;
}
