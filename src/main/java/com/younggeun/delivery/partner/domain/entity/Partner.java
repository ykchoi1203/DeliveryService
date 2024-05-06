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
import java.util.ArrayList;
import java.util.Collection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Entity
public class Partner extends BaseEntity implements UserDetails {

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

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {

        return String.valueOf(role);
      }
    });
    return collection;
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
