package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.entity.BaseEntity;
import com.younggeun.delivery.entity.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Entity
public class User extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long userId;

  @Column(unique = true)
  private String loginId;

  private String socialId;

  @Column(nullable = false)
  private String password;

  @Column(unique = true)
  private String nickname;

  @Column(unique = true)
  private String email;

  @Column(unique = true)
  private String phoneNumber;

  @Column(nullable = false)
  private RoleType role;

  private LocalDateTime deletedAt;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<String> roles = new ArrayList<>();
    roles.add(this.role.toString());

    return roles.stream().map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());
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
