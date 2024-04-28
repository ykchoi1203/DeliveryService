package com.younggeun.delivery.global.model;

import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.user.domain.dto.UserDto;
import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.type.AuthType;
import java.util.List;
import lombok.Data;

public class Auth {
  @Data
  public static class SignIn {
    private String email;
    private String password;
  }

  @Data
  public static class SignUp {
    private String email;
    private String userName;
    private String password;
    private String phoneNumber;
    private String nickname;
    private AuthType authType;
    private RoleType role;

    public User toEntity() {
      return User.builder()
          .email(this.email)
          .password(this.password)
          .phoneNumber(this.phoneNumber)
          .username(userName)
          .nickname(this.nickname)
          .authType(this.authType)
          .role(this.role)
          .build();
    }

//    public Partner toPartnerEntity() {
//      return Partner.builder()
//          .partnerId(this.userId)
//          .password(this.password)
//          .name(this.userName)
//          .phoneNumber(this.phoneNumber)
//          .roles(this.roles)
//          .build();
//    }
  }
}
