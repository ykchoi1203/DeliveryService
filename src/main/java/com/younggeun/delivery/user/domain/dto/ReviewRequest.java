package com.younggeun.delivery.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ReviewRequest  {
  private int stars;
  private String comment;
  public ReviewDto getReviewDto() {
    return ReviewDto.builder().starRate(stars).comment(comment).build();
  }
}
