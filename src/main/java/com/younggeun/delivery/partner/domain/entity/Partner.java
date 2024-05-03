package com.younggeun.delivery.partner.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.global.entity.RoleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedEntityGraph;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Entity
@SQLRestriction("deletedAt = null")
public class Partner extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long partnerId;

  @Column(unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  private String partnerName;

  @Column(unique = true)
  private String phoneNumber;

  private String address;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleType role;

  private LocalDateTime deletedAt;

  public void setDeletedAt() {
    this.deletedAt = LocalDateTime.now();
  }


}
