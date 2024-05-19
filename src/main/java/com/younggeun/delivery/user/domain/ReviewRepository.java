package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {

  Page<Review> findAllByOrderTableUserUserId(Long userId, Pageable pageable);
}
