package com.younggeun.delivery.store.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.partner.domain.entity.Partner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Where(clause = "deleted_at is null")
public class Store extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long storeId;

  @Column(nullable = false)
  private String storeName;

  @Column(nullable = false)
  private String phone;

  @Column(nullable = false)
  private String address1;
  @Column(nullable = false)
  private String address2;
  @Column(nullable = false)
  private String address3;
  @Column(nullable = false)
  private double latitude;
  @Column(nullable = false)
  private double longitude;
  @Column(nullable = false)
  private String businessNumber;
  @Column(nullable = false)
  private LocalTime openTime;
  @Column(nullable = false)
  private LocalTime endTime;

  @Column(nullable = false)
  private Long totalStars;

  @Column(nullable = false)
  private Long totalReviews;

  @Column(nullable = false)
  private int leastOrderCost;
  @Column(nullable = false)
  private int deliveryCost;
  private String originNotation;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean accessStatus;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isOpened;

  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "partner_id")
  private Partner partner;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

}
