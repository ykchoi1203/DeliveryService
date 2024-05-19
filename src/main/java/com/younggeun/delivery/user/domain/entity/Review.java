package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.user.domain.dto.ReviewDto;
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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Where(clause = "deleted_at is null")
public class Review extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long reviewId;

  @Column(nullable = false, columnDefinition = "INT CHECK (star_rate >= 1 and star_rate <= 5)")
  private int starRate;

  private String comment;

  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "order_id", nullable = false)
  private OrderTable orderTable;

  public Review(ReviewDto reviewDto) {
    this.starRate = reviewDto.getStarRate();
    this.comment = reviewDto.getComment();
  }

  public Review(ReviewDto reviewDto, OrderTable orderTable) {
    this.starRate = reviewDto.getStarRate();
    this.comment = reviewDto.getComment();
    this.orderTable = orderTable;
  }
}
