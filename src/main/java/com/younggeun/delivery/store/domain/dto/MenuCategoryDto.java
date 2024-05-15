package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.entity.MenuCategory;
import com.younggeun.delivery.store.domain.entity.Store;
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
public class MenuCategoryDto {
  private String name;
  private int sequence;

  public MenuCategoryDto(MenuCategory menuCategory) {
    name = menuCategory.getName();
    sequence = menuCategory.getSequence();
  }

  public MenuCategory toEntity(Store store) {
    return MenuCategory.builder()
        .name(name)
        .sequence(sequence)
        .store(store)
        .build();
  }

  public MenuCategory toEntity(Store store, Long categoryId) {
    return MenuCategory.builder()
        .categoryId(categoryId)
        .name(name)
        .sequence(sequence)
        .store(store)
        .build();
  }
}
