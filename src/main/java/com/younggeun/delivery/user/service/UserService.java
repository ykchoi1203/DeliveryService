package com.younggeun.delivery.user.service;

import static com.younggeun.delivery.global.exception.type.CommonErrorCode.JSON_PROCESS_ERROR;
import static com.younggeun.delivery.global.exception.type.CommonErrorCode.KAKAO_LOGIN_ERROR;
import static com.younggeun.delivery.global.exception.type.CommonErrorCode.KAKAO_MAP_ERROR;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.ADDRESS_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_NICKNAME_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_PHONE_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_OAUTH_TYPE;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_PASSWORD_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_ADDRESS_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_WISH_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.NO_MORE_ADDRESS_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.WISH_NOT_FOUND;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.younggeun.delivery.global.config.KakaoLoginConfig;
import com.younggeun.delivery.global.config.KakaoMapConfig;
import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.dto.KakaoMapResponse;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.user.domain.DeliveryAddressRepository;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.WishRepository;
import com.younggeun.delivery.user.domain.dto.DeliveryAddressDto;
import com.younggeun.delivery.user.domain.dto.Oauth2Response;
import com.younggeun.delivery.user.domain.dto.TokenResponse;
import com.younggeun.delivery.user.domain.dto.UserDto;
import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.entity.Wish;
import com.younggeun.delivery.user.domain.type.AuthType;
import com.younggeun.delivery.user.domain.type.Oauth2Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final DeliveryAddressRepository deliveryAddressRepository;
  private final WishRepository wishRepository;
  private final StoreRepository storeRepository;
  private final KakaoMapConfig kakaoMapConfig;
  private final RestTemplate restTemplate;
  private final KakaoLoginConfig kakaoLoginConfig;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    return org.springframework.security.core.userdetails.User
        .withUsername(email)
        .password(user.getPassword())
        .roles("USER")
        .build();
  }

  // 회원가입
  public User register(Auth.SignUp user) throws RestApiException {
    if(userRepository.existsByEmail(user.getEmail())) {
      throw new RestApiException(EXIST_USER_EXCEPTION);
    }

    if(userRepository.existsByNickname(user.getNickname())) {
      throw new RestApiException(EXIST_NICKNAME_EXCEPTION);
    }

    if(userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
      throw new RestApiException(EXIST_PHONE_EXCEPTION);
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user.toEntity());
  }

  // 로그인
  public User authenticate(Auth.SignIn user) {
    var member = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!passwordEncoder.matches(user.getPassword(), member.getPassword())) {
      throw new RestApiException(MISMATCH_PASSWORD_EXCEPTION);
    }
    return member;
  }

  // 회원 정보 수정 ( 비밀번호, 닉네임, 전화번호, 이름 )
  public User updateUser(UserDto userDto, Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!authentication.getName().equals(user.getEmail())) {
      throw new RestApiException(MISMATCH_USER_EXCEPTION);
    }

    if(!user.getNickname().equals(userDto.getNickname()) && userRepository.existsByNickname(userDto.getNickname())) {
      throw new RestApiException(EXIST_NICKNAME_EXCEPTION);
    }

    if(!user.getPhoneNumber().equals(userDto.getPhoneNumber()) && userRepository.existsByPhoneNumber(userDto.getPhoneNumber())) {
      throw new RestApiException(EXIST_PHONE_EXCEPTION);
    }

    User newUser = userDto.toEntity(user, passwordEncoder.encode(userDto.getPassword()));
    newUser.setCreatedAt(user.getCreatedAt());

    return userRepository.save(newUser);

  }

  // 회원 탈퇴
  public User deleteUser(Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    user.setDeletedAt();
    return userRepository.save(user);
  }

  public List<DeliveryAddressDto> getAddress(Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    return deliveryAddressRepository.findAllByUser(user).stream().map(DeliveryAddressDto::new).toList();
  }

  @Transactional
  public DeliveryAddressDto createAddress(Authentication authentication, DeliveryAddressDto deliveryAddressDto) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    List<DeliveryAddress> list = deliveryAddressRepository.findAllByUser(user);

    if(list.size() > 4) throw new RestApiException(NO_MORE_ADDRESS_EXCEPTION);

    DeliveryAddress defaultAddress = list.stream()
        .filter(DeliveryAddress::isDefaultAddressStatus)
        .findFirst()
        .orElse(null);

    if(defaultAddress != null) {
      defaultAddress.setDefaultAddressStatus(false);
      deliveryAddressRepository.save(defaultAddress);
    }

    extracted(deliveryAddressDto);

    return new DeliveryAddressDto(deliveryAddressRepository.save(deliveryAddressDto.toEntity(user)));

  }

  @Transactional
  public DeliveryAddressDto updateAddress(Authentication authentication, DeliveryAddressDto deliveryAddressDto) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(deliveryAddressDto.getAddressId()).orElseThrow(() -> new RestApiException(ADDRESS_NOT_FOUND));
    if(!Objects.equals(deliveryAddress.getUser().getUserId(), user.getUserId()))
      throw new RestApiException(MISMATCH_USER_ADDRESS_EXCEPTION);

    if(deliveryAddressDto.isDefaultAddressStatus()) {
      DeliveryAddress beforeDefaultAddress = deliveryAddressRepository.findByUserAndDefaultAddressStatusIsTrue(user).orElse(null);

      if(beforeDefaultAddress != null) {
        beforeDefaultAddress.setDefaultAddressStatus(false);
        deliveryAddressRepository.save(beforeDefaultAddress);
      }
    }

    extracted(deliveryAddressDto);
    DeliveryAddress newDeliveryAddress = deliveryAddressDto.toEntity(deliveryAddress);
    newDeliveryAddress.setCreatedAt(deliveryAddress.getCreatedAt());


    return new DeliveryAddressDto(deliveryAddressRepository.save(newDeliveryAddress));
  }

  @Transactional
  public DeliveryAddressDto deleteAddress(Authentication authentication, Long addressId) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    DeliveryAddress deliveryAddress = deliveryAddressRepository.findById(addressId).orElseThrow(() -> new RestApiException(ADDRESS_NOT_FOUND));
    if(!Objects.equals(deliveryAddress.getUser().getUserId(), user.getUserId()))
      throw new RestApiException(MISMATCH_USER_ADDRESS_EXCEPTION);

    if(deliveryAddress.isDefaultAddressStatus()) {
      DeliveryAddress newDefaultAddress = deliveryAddressRepository.findByUserOrderByUpdatedAtDesc(user).orElse(null);
      if(newDefaultAddress != null) {
        newDefaultAddress.setDefaultAddressStatus(true);
        deliveryAddressRepository.save(newDefaultAddress);
      }
    }
    deliveryAddress.setDeletedAt(LocalDateTime.now());

    return new DeliveryAddressDto(deliveryAddressRepository.save(deliveryAddress));
  }

  public List<Wish> getWishStore(Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    return wishRepository.findAllByUserOrderByCreatedAtDesc(user);
  }


  public Wish createWishStore(Authentication authentication, Long storeId) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    Store store = storeRepository.findById(storeId).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    return wishRepository.save(new Wish(user, store));
  }

  public boolean deleteWishStore(Authentication authentication, Long wishId) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    Wish wish = wishRepository.findById(wishId).orElseThrow(() -> new RestApiException(WISH_NOT_FOUND));

    if(!Objects.equals(user.getUserId(), wish.getUser().getUserId())) {
      throw new RestApiException(MISMATCH_USER_WISH_EXCEPTION);
    }

    wishRepository.delete(wish);

    return true;
  }

  public User getUser(Authentication authentication) {
    return userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
  }

  private void extracted(DeliveryAddressDto request) {
    // 헤더
    try {
      HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(this.getHeader());


      UriComponents uriComponents = UriComponentsBuilder.fromUriString(kakaoMapConfig.getMapUrl())
          .queryParam("analyze_type", "similar")
          .queryParam("page", "1")
          .queryParam("size", "10")
          .queryParam("query", request.getAddress1() + " " + request.getAddress2() + " " + request.getAddress3())
          .encode(StandardCharsets.UTF_8) // UTF-8로 인코딩
          .build();

      URI targetUrl = uriComponents.toUri();
      ResponseEntity<Map> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, Map.class);
      KakaoMapResponse kakaoMapResponse = new KakaoMapResponse((ArrayList)responseEntity.getBody().get("documents"));
      request.setLatitude(Double.parseDouble(kakaoMapResponse.getY()));
      request.setLongitude(Double.parseDouble(kakaoMapResponse.getX()));

    } catch (HttpClientErrorException e) {
      throw new RestApiException(KAKAO_MAP_ERROR);
    }
  }

  private HttpHeaders getHeader() {
    HttpHeaders httpHeaders = new HttpHeaders();
    String auth = "KakaoAK " + kakaoMapConfig.getAdminKey();

    httpHeaders.set("Authorization", auth);
    return httpHeaders;
  }

  public User ouath2Login(String code) {
    String accessToken = "";
    String refreshToken = "";

    try {
      MultiValueMap<String, String> request = kakaoLoginConfig.setRequestBody(code);
      HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(request,
          this.getHeaderForKakaoLoginToken());

      ResponseEntity<TokenResponse> responseEntity = restTemplate.exchange
          (kakaoLoginConfig.getTokenRequestUri(), HttpMethod.POST, requestEntity, TokenResponse.class);

      accessToken = responseEntity.getBody().getAccess_token();

      System.out.println("accessToken : " + accessToken);

      refreshToken = responseEntity.getBody().getRefresh_token();

      requestEntity = new HttpEntity<>(this.getHeaderForKakaoLogin(accessToken));

      ResponseEntity<String> userInfoResponse = restTemplate.postForEntity(
          kakaoLoginConfig.getMemberInfoRequestUri(), requestEntity, String.class);
      Oauth2Response userInfo = new Oauth2Response(userInfoResponse.getBody());

      User user = userRepository.findByProvideIdAndAuthType(userInfo.getSub(), AuthType.OAUTH).orElse(null);

      if(user == null) {
        user = userRepository.findByEmail(userInfo.getEmail()).orElse(null);
        if(user != null) {
          // 로그인 페이지로 이동하여 해당 아이디로 로그인 성공 시 해당 provideId, Local -> OAUTH 업데이트.
          user.setProvideId(userInfo.getSub());
          return user;
        } else {
          // 해당 이메일로 아이디가 존재하지 않음으로 회원가입 페이지로 이동.
          // 받아온 userInfo 로 User값을 넘겨줌.
          return new User(userInfo);
        }

      } else {
        if(Oauth2Type.valueOf(user.getProvider()) != Oauth2Type.KAKAO) {
          throw new RestApiException(MISMATCH_OAUTH_TYPE);
        }
        return user;
      }

    } catch (HttpClientErrorException e) {
      throw new RestApiException(KAKAO_LOGIN_ERROR);
    } catch (JsonProcessingException e) {
      throw new RestApiException(JSON_PROCESS_ERROR);
    }
  }

  private HttpHeaders getHeaderForKakaoLoginToken() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    return httpHeaders;
  }

  private HttpHeaders getHeaderForKakaoLogin(String accessCode) {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    httpHeaders.set(KakaoLoginConfig.AUTHORIZATION_HEADER, KakaoLoginConfig.TOKEN_TYPE + accessCode);

    return httpHeaders;
  }
}
