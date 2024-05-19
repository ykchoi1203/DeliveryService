package com.younggeun.delivery.user.controller;

import com.younggeun.delivery.user.domain.dto.ReviewRequest;
import com.younggeun.delivery.user.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/users/review")
@RequiredArgsConstructor
public class ReviewController {
  private final ReviewService reviewService;

  @Value("${review.photo.baseLocalPath}")
  private String reviewBaseLocalPath;

  @Value("${review.photo.baseUrlPath}")
  private String reviewBaseUrlPath;

  @Operation(summary = "리뷰 목록 조회", description = "authentication")
  @GetMapping
  public ResponseEntity<?> getReviewService(Authentication authentication, Pageable pageable) {
    var result = reviewService.getReviewService(authentication, pageable);

    return ResponseEntity.ok(result);
  }

  @Operation(summary = "리뷰 상세 조회", description = "authentication")
  @GetMapping("/{reviewId}")
  public ResponseEntity<?>  getReviewDetail(Authentication authentication, @PathVariable String reviewId) {
    var result = reviewService.getReview(authentication, reviewId);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "리뷰 작성 ", description = "authentication")
  @PostMapping(value = "/{orderId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?>  createReview(Authentication authentication,
      @PathVariable String orderId, @RequestPart(value = "file", required = false) MultipartFile file, @Valid @RequestPart(value = "request") ReviewRequest request) {
    var result = reviewService.createReview(authentication, orderId, request, file, reviewBaseUrlPath, reviewBaseLocalPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "리뷰 수정 ", description = "authentication")
  @PutMapping("/{reviewId}")
  public ResponseEntity<?>  updateReview(Authentication authentication,
      @PathVariable String reviewId, @RequestPart(value = "file", required = false) MultipartFile file, @Valid @RequestPart(value = "request") ReviewRequest request) {
    var result = reviewService.updateReview(authentication, reviewId, request, file, reviewBaseUrlPath, reviewBaseLocalPath);
    return ResponseEntity.ok(result);
  }

  @Operation(summary = "리뷰 삭제 ", description = "authentication")
  @PutMapping("/{reviewId}/delete")
  public ResponseEntity<?>  deleteReview(Authentication authentication,
      @PathVariable String reviewId) {
    var result = reviewService.deleteReview(authentication, reviewId);
    return ResponseEntity.ok(result);
  }

}
