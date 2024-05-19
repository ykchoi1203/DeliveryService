package com.younggeun.delivery.user.service;

import static com.younggeun.delivery.global.exception.type.PayErrorCode.ORDER_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_DOCUMENT_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.CANNOT_UPDATE_REVIEW;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.CANNOT_WRITE_REVIEW;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_ORDER;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.REVIEW_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.STAR_RATING_DISABLED;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.documents.StoreDocument;
import com.younggeun.delivery.store.domain.documents.repository.StoreDocumentRepository;
import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.OrderRepository;
import com.younggeun.delivery.user.domain.ReviewPhotoRepository;
import com.younggeun.delivery.user.domain.ReviewRepository;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.dto.ReviewDto;
import com.younggeun.delivery.user.domain.dto.ReviewRequest;
import com.younggeun.delivery.user.domain.entity.OrderTable;
import com.younggeun.delivery.user.domain.entity.Review;
import com.younggeun.delivery.user.domain.entity.ReviewPhoto;
import com.younggeun.delivery.user.domain.entity.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@Service
public class ReviewService {

  private final ReviewRepository reviewRepository;
  private final ReviewPhotoRepository reviewPhotoRepository;
  private final StoreRepository storeRepository;
  private final UserRepository userRepository;
  private final ElasticsearchRestTemplate elasticsearchRestTemplate;
  private final StoreDocumentRepository storeDocumentRepository;
  private final OrderRepository orderRepository;

  public Page<ReviewDto> getReviewService(Authentication authentication, Pageable pageable) {
    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    Page<Review> reviewPage = reviewRepository.findAllByOrderTableUserUserId(user.getUserId(),
        pageable);

    List<Long> reviewIdList = reviewPage.map(Review::getReviewId).toList();

    Map<Long, ReviewPhoto> reviewPhotoList = reviewPhotoRepository.findAllByReviewReviewIdIn(
        reviewIdList).stream().collect(
        Collectors.toMap(item -> item.getReview().getReviewId(), Function.identity()));
    return reviewPage.map(review -> {
      ReviewDto reviewDto = new ReviewDto(review);
      reviewDto.setReviewPhoto(new PhotoDto(reviewPhotoList.get(review.getReviewId())));
      return reviewDto;
    });
  }

  public Object getReview(Authentication authentication, String reviewId) {
   Review review = matchUserAndReview(authentication, reviewId);

    ReviewPhoto reviewPhoto = reviewPhotoRepository.findByReview(review);

    return new ReviewDto(review, new PhotoDto(reviewPhoto));

  }

  @Transactional
  public ReviewDto createReview(Authentication authentication, String orderId, ReviewRequest request,
      MultipartFile file, String reviewBaseUrlPath, String reviewBaseLocalPath) {
    System.out.println(request.toString());
    if(request.getStars() < 1 || request.getStars() > 5) {
      throw new RestApiException(STAR_RATING_DISABLED);
    }

    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    OrderTable orderTable = orderRepository.findById(Long.parseLong(orderId))
        .orElseThrow(() -> new RestApiException(ORDER_NOT_FOUND));

    if (orderTable.getUser().getUserId() != user.getUserId()) {
      throw new RestApiException(MISMATCH_USER_ORDER);
    }

    System.out.println(orderTable.getStatus());

    if(orderTable.getStatus() != OrderStatus.DELIVERY_COMPLETED) {
      throw new RestApiException(CANNOT_WRITE_REVIEW);
    }

    if(orderTable.getDeliveryTime().isBefore(LocalDateTime.now().minusDays(3))) {
      throw new RestApiException(CANNOT_WRITE_REVIEW);
    }

    Review review = new Review(request.getReviewDto(), orderTable);

    review = reviewRepository.save(review);
    ReviewPhoto reviewPhoto = new ReviewPhoto();
    if(file != null) {
      PhotoDto reviewPhotoDto = new PhotoDto();
      reviewPhotoDto.savePhotos(file,  reviewBaseLocalPath, reviewBaseUrlPath);
      reviewPhoto = reviewPhotoRepository.save(new ReviewPhoto(reviewPhotoDto, review));
    }

    updateStoreAndStoreDocument(orderTable.getStore().getStoreId(), review.getStarRate(), 1);

    return new ReviewDto(review, new PhotoDto(reviewPhoto));

  }

  @Transactional
  public ReviewDto updateReview(Authentication authentication, String reviewId, ReviewRequest request, MultipartFile file, String reviewBaseUrlPath, String reviewBaseLocalPath) {
    if(request.getStars() < 1 || request.getStars() > 5) {
      throw new RestApiException(STAR_RATING_DISABLED);
    }

    Review review = matchUserAndReview(authentication, reviewId);

    if(review.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7))) {
      throw new RestApiException(CANNOT_UPDATE_REVIEW);
    }

    int beforeStar = review.getStarRate();

    review.setComment(request.getComment());
    review.setStarRate(request.getStars());

    review = reviewRepository.save(review);

    ReviewPhoto reviewPhoto = reviewPhotoRepository.findByReviewReviewId(review.getReviewId()).orElse(null);

    if(reviewPhoto != null) {
      reviewPhoto.setDeletedAt(LocalDateTime.now());
      reviewPhotoRepository.save(reviewPhoto);
    }

    PhotoDto reviewPhotoDto = new PhotoDto();
    reviewPhotoDto.savePhotos(file, reviewBaseLocalPath, reviewBaseUrlPath);

    reviewPhoto = reviewPhotoRepository.save(new ReviewPhoto(reviewPhotoDto, review));
    updateStoreAndStoreDocument(review.getOrderTable().getStore().getStoreId(), request.getStars()- beforeStar, 0);

    return new ReviewDto(review, new PhotoDto(reviewPhoto));
  }

  @Transactional
  public boolean deleteReview(Authentication authentication, String reviewId) {

    Review review = matchUserAndReview(authentication, reviewId);


    review.setDeletedAt(LocalDateTime.now());

    ReviewPhoto reviewPhoto = reviewPhotoRepository.findByReviewReviewId(review.getReviewId()).orElse(null);

    if(reviewPhoto != null) {
      reviewPhoto.setDeletedAt(LocalDateTime.now());
      reviewPhotoRepository.save(reviewPhoto);
    }

    reviewRepository.save(review);

    updateStoreAndStoreDocument(review.getOrderTable().getStore().getStoreId(), -review.getStarRate(), -1);

    return true;
  }

  private Review matchUserAndReview(Authentication authentication, String reviewId) {
    User user = userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    Review review = reviewRepository.findById(Long.parseLong(reviewId))
        .orElseThrow(() -> new RestApiException(REVIEW_NOT_FOUND));

    if (review.getOrderTable().getUser().getUserId() != user.getUserId()) {
      throw new RestApiException(MISMATCH_USER_ORDER);
    }

    return review;
  }

  private void updateStoreAndStoreDocument(Long storeId, int starRate, int plusReviewCount) {
    Store store = storeRepository.findById(storeId).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    store.setTotalReviews(store.getTotalReviews() + plusReviewCount);
    store.setTotalStars(store.getTotalStars() + starRate);

    store = storeRepository.save(store);
    StoreDocument storeDocument = storeDocumentRepository.findById(store.getStoreId()).orElseThrow(() -> new RestApiException(STORE_DOCUMENT_NOT_FOUND));
    storeDocument.setStars(store.getTotalStars() / (double) (store.getTotalReviews() != 0 ? store.getTotalReviews() : 1));

    elasticsearchRestTemplate.save(storeDocument);
  }

}
