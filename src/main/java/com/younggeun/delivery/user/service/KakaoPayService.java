package com.younggeun.delivery.user.service;

import static com.younggeun.delivery.global.exception.type.UserErrorCode.CART_IS_EMPTY;

import com.younggeun.delivery.global.config.KakaoPayConfig;
import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.user.domain.dto.CartDto;
import com.younggeun.delivery.user.domain.dto.KakaoApproveResponse;
import com.younggeun.delivery.user.domain.dto.KakaoCancelResponse;
import com.younggeun.delivery.user.domain.dto.KakaoReadyResponse;
import com.younggeun.delivery.user.domain.dto.KakaoRequestDto;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@AllArgsConstructor
public class KakaoPayService {

  private final ShoppingCartService cartService;
  private final KakaoPayConfig kakaoPayConfig;
  private final RestTemplate restTemplate;
  static final String cid = "TC0ONETIME";
  public KakaoReadyResponse kakaoPayReady(Authentication authentication) {
    List<CartDto> cartDtoList = cartService.getCart(authentication.getName());
    KakaoRequestDto kakaoRequestDto = new KakaoRequestDto();
    // 카카오페이 요청 양식
    Map<String, String> parameters = getReadyParameters(authentication.getName(), cartDtoList, kakaoRequestDto);
    // 파라미터, 헤더
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeadersToDevKey());

    try {
      ResponseEntity<KakaoReadyResponse> responseEntity = restTemplate.exchange(
          kakaoPayConfig.getReadyUrlDev(), HttpMethod.POST, requestEntity, KakaoReadyResponse.class);

      kakaoRequestDto.setTid(responseEntity.getBody().getTid());
      responseEntity.getBody().setRequestDto(kakaoRequestDto);
      return responseEntity.getBody();
    } catch (HttpClientErrorException e) {
      throw new HttpClientErrorException(e.getStatusCode(), e.getMessage());
    }
  }

  private Map<String, String> getReadyParameters(String userEmail,
      List<CartDto> cartDtoList, KakaoRequestDto kakaoRequestDto) {
    int quantity = cartDtoList.stream().mapToInt(CartDto::getQuantity).sum();
    int totalCost = cartDtoList.stream().mapToInt(CartDto::getTotalCost).sum();
    kakaoRequestDto.setItemName(cartDtoList.stream().findFirst().orElseThrow(() -> new RestApiException(CART_IS_EMPTY)).getMenu().getMenuName() + (cartDtoList.size() > 1 ? "외 " + (quantity-1) : "개"));
    kakaoRequestDto.setQuantity(quantity);
    kakaoRequestDto.setTotalPrice(totalCost);
    kakaoRequestDto.setVatAmount(totalCost/11);
    kakaoRequestDto.setTexFreeAmount(0);
    kakaoRequestDto.setPartnerUserId(userEmail);
    kakaoRequestDto.setPartnerOrderId(userEmail + LocalDateTime.now().withNano(0));

    Map<String, String> parameters = new HashMap<>();
    parameters.put("cid", cid);
    parameters.put("partner_order_id", kakaoRequestDto.getPartnerOrderId());
    parameters.put("partner_user_id", kakaoRequestDto.getPartnerUserId());
    parameters.put("item_name", kakaoRequestDto.getItemName());
    parameters.put("quantity", Integer.toString(kakaoRequestDto.getQuantity()));
    parameters.put("total_amount", Integer.toString(kakaoRequestDto.getTotalPrice()));
    parameters.put("vat_amount", Integer.toString(kakaoRequestDto.getVatAmount()));
    parameters.put("tax_free_amount", Integer.toString(kakaoRequestDto.getTexFreeAmount()));
    parameters.put("approval_url", "http://localhost:8080/users/payment/kakao/success"); // 성공 시 redirect url
    parameters.put("cancel_url", "http://localhost:8080/users/payment/kakao/cancel"); // 취소 시 redirect url
    parameters.put("fail_url", "http://localhost:8080/users/payment/kakao/fail"); // 실패 시 redirect url

    return parameters;
  }

  /**
   * 결제 완료 승인
   */
  public String approveResponse(KakaoRequestDto request, Authentication authentication,
      String header) {
    // 카카오 요청
    Map<String, String> parameters = new HashMap<>();
    parameters.put("cid", cid);
    parameters.put("tid", request.getTid());
    parameters.put("partner_order_id", request.getPartnerOrderId());
    parameters.put("partner_user_id", request.getPartnerUserId());
    parameters.put("pg_token", request.getPgToken());

    // 파라미터, 헤더
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeadersToDevKey());

    // 외부에 보낼 url
    ResponseEntity<KakaoApproveResponse> responseEntity = restTemplate.exchange(
        kakaoPayConfig.getApproveUrlDev(), HttpMethod.POST, requestEntity, KakaoApproveResponse.class);

    // POST 요청 생성
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", header);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<KakaoApproveResponse> newRequestEntity = new HttpEntity<>(responseEntity.getBody(), headers);

    // POST 요청 보내기
    ResponseEntity<String> newResponseEntity = restTemplate.postForEntity("http://localhost:8080/users/order/success", newRequestEntity, String.class);

    return newResponseEntity.getBody();
  }

  /**
   * 결제 환불
   */
  public KakaoCancelResponse kakaoCancel(Authentication authentication) {

    // 카카오페이 요청
    Map<String, String> parameters = new HashMap<>();
    parameters.put("cid", cid);
    parameters.put("tid", "환불할 결제 고유 번호");
    parameters.put("cancel_amount", "환불 금액");
    parameters.put("cancel_tax_free_amount", "환불 비과세 금액");
    parameters.put("cancel_vat_amount", "환불 부가세");

    // 파라미터, 헤더
    HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeadersToDevKey());

    // 외부에 보낼 url
    ResponseEntity<KakaoCancelResponse> responseEntity = restTemplate.exchange(
        kakaoPayConfig.getCancelUrlDev(), HttpMethod.POST, requestEntity, KakaoCancelResponse.class);

    return responseEntity.getBody();
  }

  /**
   * 카카오 요구 헤더값
   */
  private HttpHeaders getHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    String auth = "KakaoAK " + kakaoPayConfig.getAdminKey();

    httpHeaders.set("Authorization", auth);
    httpHeaders.set("Content-type",  "application/x-www-form-urlencoded;charset=utf-8");

    return httpHeaders;
  }

  private HttpHeaders getHeadersToDevKey() {
    HttpHeaders httpHeaders = new HttpHeaders();
    String auth = "SECRET_KEY " + kakaoPayConfig.getDevKey();

    httpHeaders.set("Authorization", auth);
    httpHeaders.set("Content-type", "application/json");

    return httpHeaders;
  }

}
