package com.younggeun.delivery.store.domain.dto;

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
public class MenuDto {
  private Long menuId;
  private String menuName;

  private int price;
  private String description;
  private Long categoryId;
  private boolean soldOutStatus;
}
