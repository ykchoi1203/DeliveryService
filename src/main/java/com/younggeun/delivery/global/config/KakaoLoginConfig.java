package com.younggeun.delivery.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kakaologin")
public class KakaoLoginConfig {
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String GRANT_TYPE = "authorization_code";
  public static final String TOKEN_TYPE = "Bearer ";

  private String tokenRequestUri;

  private String redirectUri;

  private String adminKey;

  private String memberInfoRequestUri;

  private String authorizeUri;

  public MultiValueMap<String, String> setRequestBody(String code) {
    MultiValueMap<String, String> request = new LinkedMultiValueMap<>();

    request.add("grant_type", "authorization_code");
    request.add("client_id", adminKey);
    request.add("redirect_uri", redirectUri);
    request.add("code", code);

    return request;
  }

}
