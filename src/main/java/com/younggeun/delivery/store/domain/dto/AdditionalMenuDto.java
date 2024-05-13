package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.store.domain.entity.Menu;
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
public class AdditionalMenuDto implements Comparable<AdditionalMenuDto> {
  private Long additionalMenuId;
  private String menuName;

  private int price;
  private int sequence;
  private Long menuId;
  private boolean soldOutStatus;

  public AdditionalMenuDto(AdditionalMenu additionalMenu) {
    this.additionalMenuId = additionalMenu.getAdditionalMenuId();
    this.menuName = additionalMenu.getMenuName();
    this.price = additionalMenu.getPrice();
    this.sequence = additionalMenu.getSequence();
    this.menuId = additionalMenu.getMenu().getMenuId();
    this.soldOutStatus = additionalMenu.isSoldOutStatus();
  }

  public AdditionalMenu toEntity(Menu menu) {
    return AdditionalMenu.builder()
        .menuName(menuName)
        .sequence(sequence)
        .price(price)
        .soldOutStatus(true)
        .menu(menu)
        .build();
  }

  @Override
  public int compareTo(AdditionalMenuDto o) {
    return sequence - o.getSequence();
  }

}
