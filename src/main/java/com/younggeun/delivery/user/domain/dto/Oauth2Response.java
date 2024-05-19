package com.younggeun.delivery.user.domain.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Oauth2Response {
  private String sub;
  private String email;
  private String name;
  private String phone;
  private String nickname;

  public Oauth2Response(String body) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    HashMap resultMap = mapper.readValue(body, HashMap.class);
    this.sub = String.valueOf(resultMap.get("id"));

    HashMap<String,Object> properties = (HashMap<String, Object>) resultMap.get("properties");
    if(properties.containsKey("nickname"))
      this.nickname = (String) properties.get("nickname");

    HashMap<String,Object> kakaoAccount = (HashMap<String, Object>) resultMap.get("kakao_account");
    this.email= (String) kakaoAccount.get("email");
    this.phone = "0" + ((String) kakaoAccount.get("phone_number")).split(" ")[1];
    this.name = (String) kakaoAccount.get("name");
  }
}
