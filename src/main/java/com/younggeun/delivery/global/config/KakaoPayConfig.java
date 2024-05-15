package com.younggeun.delivery.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "kakaopay")
public class KakaoPayConfig {
  private String adminKey;
  private String devKey;
  private String cid;
  private String readyUrlDev;
  private String approveUrlDev;
  private String cancelUrlDev;
}