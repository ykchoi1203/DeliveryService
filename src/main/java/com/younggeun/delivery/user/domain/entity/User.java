package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.user.domain.dto.Oauth2Response;
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
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Entity
@Where(clause = "deleted_at is null")
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String username;

  @Column(unique = true)
  private String nickname;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private AuthType authType;

  private String provider; //어떤 OAuth인지(google, naver 등)
  private String provideId; // 해당 OAuth 의 key(id)

  @Column(unique = true)
  private String phoneNumber;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private RoleType role;

  private LocalDateTime deletedAt;

  public User(Oauth2Response userInfo) {
    this.provideId = userInfo.getSub();
    this.email = userInfo.getEmail();
    this.username = userInfo.getName();
    this.nickname = userInfo.getNickname();
    this.phoneNumber = userInfo.getPhone();
    this.authType = AuthType.OAUTH;
  }

  public void setProvideId(String provideId) {
    this.provideId = provideId;
  }
  public void setDeletedAt() {
    this.deletedAt = LocalDateTime.now();
  }

}
