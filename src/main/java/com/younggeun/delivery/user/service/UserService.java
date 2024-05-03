package com.younggeun.delivery.user.service;

import com.younggeun.delivery.global.exception.impl.AlreadyExistPhoneNumberException;
import com.younggeun.delivery.global.exception.impl.AlreadyExistUserException;
import com.younggeun.delivery.global.exception.impl.MisMatchUserException;
import com.younggeun.delivery.global.exception.impl.PasswordMismatchException;
import com.younggeun.delivery.global.exception.impl.UserNotFoundException;
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
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    return org.springframework.security.core.userdetails.User
        .withUsername(email)
        .password(user.getPassword())
        .roles("USER")
        .build();
  }

  // 회원가입
  public User register(Auth.SignUp user) {
    if(userRepository.existsByEmail(user.getEmail())) {
      throw new AlreadyExistUserException();
    }

    if(userRepository.existsByNickname(user.getNickname())) {
      throw new AlreadyExistUserException();
    }

    if(userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
      throw new AlreadyExistPhoneNumberException();
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));


    return userRepository.save(user.toEntity());
  }

  // 로그인
  public User authenticate(Auth.SignIn user) {
    var member = userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);

    if(!passwordEncoder.matches(user.getPassword(), member.getPassword())) {
      throw new PasswordMismatchException();
    }
    return member;
  }

  // 회원 정보 수정 ( 비밀번호, 닉네임, 전화번호, 이름 )
  public User updateUser(UserDto userDto, Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new);

    if(!authentication.getName().equals(user.getEmail())) {
      throw new MisMatchUserException();
    }

    if(!user.getNickname().equals(userDto.getNickname()) && userRepository.existsByNickname(userDto.getNickname())) {
      throw new AlreadyExistUserException();
    }

    if(!user.getPhoneNumber().equals(userDto.getPhoneNumber()) && userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
      throw new AlreadyExistPhoneNumberException();
    }

    return userRepository.save(User.builder()
                                      .userId(user.getUserId())
                                      .username(userDto.getUsername())
                                      .email(user.getEmail())
                                      .authType(user.getAuthType())
                                      .provideId(user.getProvideId())
                                      .provider(user.getProvider())
                                      .role(user.getRole())
                                      .phoneNumber(userDto.getPhoneNumber())
                                      .nickname(userDto.getNickname())
                                      .password(passwordEncoder.encode(userDto.getPassword())).build()
                              );

  }

  // 회원 탈퇴
  public User deleteUser(Authentication authentication) {
    User user = userRepository.findByEmail(authentication.getName()).orElseThrow(UserNotFoundException::new);
    user.setDeletedAt();
    return userRepository.save(user);
  }
}
