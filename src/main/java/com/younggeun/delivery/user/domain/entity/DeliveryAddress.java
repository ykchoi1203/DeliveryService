package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
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
public class DeliveryAddress extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long addressId;
  @Column(nullable = false)
  private String name;
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
  private boolean defaultAddressStatus;

  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

}
