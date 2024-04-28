package com.younggeun.delivery.user.domain.dto;

import jakarta.persistence.NamedEntityGraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
public class UserDto {
  private String password;
  private String username;
  private String nickname;
  private String phoneNumber;
}
