package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.entity.MenuCategory;
import java.util.List;
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
public class MenuListDto {
  private Long categoryId;
  private String name;
  private int sequence;

  private List<MenuDto> menuList;

  public MenuListDto(MenuCategory menuCategory) {
    this.categoryId = menuCategory.getCategoryId();
    this.name = menuCategory.getName();
    this.sequence = menuCategory.getSequence();
  }

}
