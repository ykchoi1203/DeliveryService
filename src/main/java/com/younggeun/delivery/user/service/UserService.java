package com.younggeun.delivery.user.service;

import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_NICKNAME_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_PHONE_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.EXIST_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_PASSWORD_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.dto.UserDto;
import com.younggeun.delivery.user.domain.entity.User;
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
public class UserService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

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

    return userRepository.save(userDto.toEntity(user, passwordEncoder.encode(userDto.getPassword())));

  }

  // 회원 탈퇴
  public User deleteUser(Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
    user.setDeletedAt();
    return userRepository.save(user);
  }
}
