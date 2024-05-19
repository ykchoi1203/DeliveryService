package com.younggeun.delivery.store.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.dto.StoreDto;
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
import lombok.ToString;
import org.hibernate.annotations.Where;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString
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

  @Column(nullable = false, columnDefinition = "INT CHECK (total_stars >= 0)")
  private Long totalStars;

  @Column(nullable = false, columnDefinition = "INT CHECK (total_reviews >= 0)")
  private Long totalReviews;

  @Column(nullable = false, columnDefinition = "INT CHECK (least_order_cost >= 0)")
  private int leastOrderCost;
  @Column(nullable = false, columnDefinition = "INT CHECK (delivery_cost >= 0)")
  private int deliveryCost;
  private String originNotation;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean accessStatus;

  @Column(nullable = false, columnDefinition = "boolean default false")
  private boolean isOpened;

  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "partner_id", nullable = false)
  private Partner partner;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  public void update(StoreDto storeDto) {
    this.storeName = storeDto.getStoreName();
    this.address1 = storeDto.getAddress1();
    this.address2 = storeDto.getAddress2();
    this.address3 = storeDto.getAddress3();
    this.openTime = storeDto.getOpenTime();
    this.latitude = storeDto.getLatitude();
    this.longitude = storeDto.getLongitude();
    this.endTime = storeDto.getEndTime();
    this.leastOrderCost = storeDto.getLeastOrderCost();
    this.deliveryCost = storeDto.getDeliveryCost();
    this.originNotation = storeDto.getOriginNotation();
  }
}
