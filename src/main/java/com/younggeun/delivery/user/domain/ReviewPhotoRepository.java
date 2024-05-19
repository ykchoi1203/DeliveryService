package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.Review;
import com.younggeun.delivery.user.domain.entity.ReviewPhoto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPhotoRepository extends JpaRepository<ReviewPhoto, Long> {

  List<ReviewPhoto> findAllByReviewReviewIdIn(List<Long> reviewIdList);

  ReviewPhoto findByReview(Review review);

  Optional<ReviewPhoto> findByReviewReviewId(Long reviewId);
}
