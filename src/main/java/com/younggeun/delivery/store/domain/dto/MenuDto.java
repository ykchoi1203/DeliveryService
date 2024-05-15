package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuCategory;
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
public class MenuDto {
  private Long menuId;
  private String menuName;

  private int price;
  private String description;
  private Long categoryId;
  private boolean soldOutStatus;
  private MenuCategoryDto menuCategory;

  private PhotoDto menuPhoto;
  private List<AdditionalMenuDto> additionalMenuList;

  public MenuDto(Long menuId, String menuName, int price, String description, boolean soldOutStatus, Long categoryId) {
    this.menuId = menuId;
    this.menuName = menuName;
    this.price = price;
    this.description = description;
    this.soldOutStatus = soldOutStatus;
    this.categoryId = categoryId;
  }

  public MenuDto(Menu menu) {
    this.menuId = menu.getMenuId();
    this.menuName = menu.getMenuName();
    this.price = menu.getPrice();
    this.description = menu.getDescription();
    this.soldOutStatus = menu.isSoldOutStatus();
    this.menuCategory = new MenuCategoryDto(menu.getMenuCategory());
    this.categoryId = menu.getMenuCategory().getCategoryId();
  }

  public Menu toEntity(MenuCategory menuCategory) {
    return Menu.builder()
        .menuName(menuName)
        .price(price)
        .description(description)
        .soldOutStatus(false)
        .menuCategory(menuCategory)
        .build();
  }

  public Menu toEntity(Long menuId, MenuCategory menuCategory) {
    return Menu.builder()
        .menuId(menuId)
        .menuName(menuName)
        .price(price)
        .description(description)
        .soldOutStatus(soldOutStatus)
        .menuCategory(menuCategory)
        .build();
  }
}
