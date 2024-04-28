package com.younggeun.delivery.global.model;

import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.type.AuthType;
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
    private String address;

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

    public Partner toPartnerEntity() {
      return Partner.builder()
          .email(this.email)
          .password(this.password)
          .partnerName(this.userName)
          .phoneNumber(this.phoneNumber)
          .address(this.address)
          .role(this.role)
          .build();
    }
  }
}
