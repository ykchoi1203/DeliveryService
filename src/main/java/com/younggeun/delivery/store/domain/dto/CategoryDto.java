package com.younggeun.delivery.store.domain.dto;

import com.younggeun.delivery.store.domain.entity.Category;
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
public class CategoryDto {
  private Long categoryId;
  private String name;
  private int sequence;

  public Category toEntity() {
    return Category.builder()
        .name(name)
        .sequence(sequence)
        .build();
  }

  public Category toEntity(Long categoryId) {
    return Category.builder()
        .categoryId(categoryId)
        .name(name)
        .sequence(sequence)
        .build();
  }
}
