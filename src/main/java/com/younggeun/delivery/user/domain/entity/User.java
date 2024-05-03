package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.user.domain.type.AuthType;
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
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  private String username;

  @Column(unique = true)
  private String nickname;

  @Column(nullable = false)
  private AuthType authType;

  private String provider; //어떤 OAuth인지(google, naver 등)
  private String provideId; // 해당 OAuth 의 key(id)

  @Column(unique = true)
  private String phoneNumber;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleType role;

  private LocalDateTime deletedAt;

  public void setDeletedAt() {
    this.deletedAt = LocalDateTime.now();
  }

}
