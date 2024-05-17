package com.younggeun.delivery.user.domain.dto;

import com.younggeun.delivery.user.domain.entity.User;
import jakarta.persistence.NamedEntityGraph;
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
public class UserDto implements UserDetails {

  private String email;
  private String password;
  private String memberName;
  private String nickname;
  private String phoneNumber;
  private String provideId;

  public UserDto(User user) {
    this.email = user.getEmail();
    this.password = user.getPassword();
    this.memberName = user.getUsername();
    this.nickname = user.getNickname();
    this.phoneNumber = user.getPhoneNumber();
    this.provideId = user.getProvideId();
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> collection = new ArrayList<>();
    collection.add(new GrantedAuthority() {
      @Override
      public String getAuthority() {

        return "USER";
      }
    });
    return collection;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return email;
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

  public User toEntity(User user, String password) {
    return User.builder()
        .userId(user.getUserId())
        .username(memberName)
        .email(user.getEmail())
        .authType(user.getAuthType())
        .provideId(user.getProvideId())
        .provider(user.getProvider())
        .role(user.getRole())
        .phoneNumber(phoneNumber)
        .nickname(nickname)
        .password(password).build();
  }
}
