package com.younggeun.delivery.user.domain.dto;

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
public class CartMenuDto {
  private Long menuId;
  private String menuName;

  private int price;
  private Long categoryId;
  private Long storeId;
  private String description;

  public CartMenuDto(Menu menu) {
    this.menuId = menu.getMenuId();
    this.menuName = menu.getMenuName();
    this.price = menu.getPrice();
    this.categoryId = menu.getMenuCategory().getCategoryId();
    this.description = menu.getDescription();
    this.storeId = menu.getMenuCategory().getStore().getStoreId();
  }
}
