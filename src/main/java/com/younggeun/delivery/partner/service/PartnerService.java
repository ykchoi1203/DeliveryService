package com.younggeun.delivery.partner.service;

import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_PHONE_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_PASSWORD_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.partner.domain.PartnerRepository;
import com.younggeun.delivery.partner.domain.dto.PartnerDto;
import com.younggeun.delivery.partner.domain.entity.Partner;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PartnerService implements UserDetailsService {
  private final PartnerRepository partnerRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    Partner user = partnerRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return org.springframework.security.core.userdetails.User
        .withUsername(email)
        .password(user.getPassword())
        .roles("PARTNER")
        .build();
  }

  // 회원가입
  public Partner register(Auth.SignUp partner) {
    if(partnerRepository.existsByEmail(partner.getEmail())) {
      throw new RestApiException(EXIST_USER_EXCEPTION);
    }

    if(partnerRepository.existsByPhoneNumber(partner.getPhoneNumber())) {
      throw new RestApiException(EXIST_PHONE_EXCEPTION);
    }

    partner.setPassword(passwordEncoder.encode(partner.getPassword()));

    return partnerRepository.save(partner.toPartnerEntity());
  }

  // 로그인
  public Partner authenticate(Auth.SignIn user) {
    var partner = partnerRepository.findByEmail(user.getEmail()).orElseThrow(
        () -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!passwordEncoder.matches(user.getPassword(), partner.getPassword())) {
      throw new RestApiException(MISMATCH_PASSWORD_EXCEPTION);
    }
    return partner;
  }

  public Partner updatePartner(PartnerDto partnerDto, Authentication authentication) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!authentication.getName().equals(partner.getEmail())) {
      throw new RestApiException(MISMATCH_USER_EXCEPTION);
    }

    if(!partner.getPhoneNumber().equals(partnerDto.getPhoneNumber()) && partnerRepository.existsByPhoneNumber(partnerDto.getPhoneNumber())) {
      throw new RestApiException(EXIST_PHONE_EXCEPTION);
    }

    partnerDto.setPassword(passwordEncoder.encode(partnerDto.getPassword()));
    Partner updatePartner = Partner.builder()
        .partnerId(partner.getPartnerId())
        .partnerName(partnerDto.getPartnerName())
        .email(partner.getEmail())
        .role(partner.getRole())
        .phoneNumber(partnerDto.getPhoneNumber())
        .address(partnerDto.getAddress())
        .password(partnerDto.getPassword()).build();
    updatePartner.setCreatedAt(partner.getCreatedAt());
    return partnerRepository.save(updatePartner);

  }

  public Object deletePartner(Authentication authentication) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(()-> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!authentication.getName().equals(partner.getEmail())) {
      throw new RestApiException(MISMATCH_USER_EXCEPTION);
    }

    partner.setDeletedAt();

    return partnerRepository.save(partner);
  }

  public Partner getPartner(Authentication authentication) {
    return partnerRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
  }
}
