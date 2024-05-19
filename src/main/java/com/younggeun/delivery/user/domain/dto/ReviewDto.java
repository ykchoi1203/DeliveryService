package com.younggeun.delivery.user.domain.dto;

import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.user.domain.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ReviewDto {
  private Long reviewId;
  private Long storeId;
  private String storeName;
  private Long userId;
  private String userNickname;

  private String comment;
  private int starRate;

  private PhotoDto reviewPhoto;

  public ReviewDto(Review review) {
    this.reviewId = review.getReviewId();
    this.storeId = review.getOrderTable().getStore().getStoreId();
    this.storeName = review.getOrderTable().getStore().getStoreName();
    this.userId = review.getOrderTable().getUser().getUserId();
    this.userNickname = review.getOrderTable().getUser().getNickname();
    this.comment = review.getComment();
    this.starRate = review.getStarRate();
  }

  public ReviewDto(Review review, PhotoDto reviewPhoto) {
    this.reviewId = review.getReviewId();
    this.storeId = review.getOrderTable().getStore().getStoreId();
    this.storeName = review.getOrderTable().getStore().getStoreName();
    this.userId = review.getOrderTable().getUser().getUserId();
    this.userNickname = review.getOrderTable().getUser().getNickname();
    this.comment = review.getComment();
    this.starRate = review.getStarRate();
    this.reviewPhoto = reviewPhoto;
  }
}
