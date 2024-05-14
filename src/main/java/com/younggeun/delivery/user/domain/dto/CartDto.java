package com.younggeun.delivery.user.domain.dto;

import com.younggeun.delivery.store.domain.dto.AdditionalMenuDto;
import java.util.List;
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
public class CartDto {
  private Long cartId;
  private Long storeId;
  private int totalCost;
  private CartMenuDto menu;
  private int quantity;
  private List<AdditionalMenuDto> additionalMenuList;
}
