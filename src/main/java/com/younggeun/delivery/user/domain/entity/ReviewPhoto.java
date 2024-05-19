package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.store.domain.dto.PhotoDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Where(clause = "deleted_at is null")
public class ReviewPhoto extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewPhotoId;

  @Column(nullable = false)
  private String url;
  @Column(nullable = false)
  private String photoName;

  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "review_id", nullable = false)
  private Review review;

  public ReviewPhoto(PhotoDto reviewPhotoDto, Review review) {
    this.review = review;
    this.url = reviewPhotoDto.getUrl();
    this.photoName = reviewPhotoDto.getPhotoName();
  }
}
