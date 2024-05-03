package com.younggeun.delivery.partner.service;

import com.younggeun.delivery.global.exception.impl.AlreadyExistPhoneNumberException;
import com.younggeun.delivery.global.exception.impl.AlreadyExistUserException;
import com.younggeun.delivery.global.exception.impl.MisMatchUserException;
import com.younggeun.delivery.global.exception.impl.PasswordMismatchException;
import com.younggeun.delivery.global.exception.impl.UserNotFoundException;
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
      throw new AlreadyExistUserException();
    }

    if(partnerRepository.existsByPhoneNumber(partner.getPhoneNumber())) {
      throw new AlreadyExistPhoneNumberException();
    }

    partner.setPassword(passwordEncoder.encode(partner.getPassword()));

    return partnerRepository.save(partner.toPartnerEntity());
  }

  // 로그인
  public Partner authenticate(Auth.SignIn user) {
    var partner = partnerRepository.findByEmail(user.getEmail()).orElseThrow(
        UserNotFoundException::new);

    if(!passwordEncoder.matches(user.getPassword(), partner.getPassword())) {
      throw new PasswordMismatchException();
    }
    return partner;
  }

  public Partner updatePartner(PartnerDto partnerDto, Authentication authentication) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new);

    if(!authentication.getName().equals(partner.getEmail())) {
      throw new MisMatchUserException();
    }

    if(!partner.getPhoneNumber().equals(partnerDto.getPhoneNumber()) && partnerRepository.existsByPhoneNumber(partner.getPhoneNumber())) {
      throw new AlreadyExistPhoneNumberException();
    }

    return partnerRepository.save(Partner.builder()
        .partnerId(partner.getPartnerId())
        .partnerName(partnerDto.getPartnerName())
        .email(partner.getEmail())
        .role(partner.getRole())
        .phoneNumber(partnerDto.getPhoneNumber())
        .address(partnerDto.getAddress())
        .password(passwordEncoder.encode(partnerDto.getPassword())).build()
    );

  }

  public Object deletePartner(Authentication authentication) {
    Partner partner = partnerRepository.findByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new);

    if(!authentication.getName().equals(partner.getEmail())) {
      throw new MisMatchUserException();
    }

    partner.setDeletedAt();

    return partnerRepository.save(partner);
  }
}
