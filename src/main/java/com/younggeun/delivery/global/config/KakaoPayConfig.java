package com.younggeun.delivery.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KakaoPayConfig {
  public static String adminKey;
  public static String devKey;
  public static String cid;
  public static String readyUrlDev;
  public static String approveUrlDev;

  public static String cancelUrlDev;

  @Value("${kakaopay.admin-key}")
  public void setAdminKey(String adminKey) {
    KakaoPayConfig.adminKey = adminKey;
  }

  @Value("${kakaopay.cid}")
  public void setCid(String cid) {
    KakaoPayConfig.cid = cid;
  }

  @Value("${kakaopay.dev-key}")
  public void setDevKey(String devKey) {
    KakaoPayConfig.devKey = devKey;
  }

  @Value("${kakaopay.ready-url-dev}")
  public void setReadyUrlDev(String readyUrlDev) {
    KakaoPayConfig.readyUrlDev = readyUrlDev;
  }

  @Value("${kakaopay.approve-url-dev}")
  public void setApproveUrlDev(String approveUrlDev) {
    KakaoPayConfig.approveUrlDev = approveUrlDev;
  }

  @Value("${kakaopay.cancel-url-dev}")
  public void setCancelUrlDev(String cancelUrlDev) {
    KakaoPayConfig.cancelUrlDev = cancelUrlDev;
  }



}