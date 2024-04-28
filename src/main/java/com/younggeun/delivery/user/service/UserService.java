package com.younggeun.delivery.user.service;

import com.younggeun.delivery.global.exception.impl.AlreadyExistUserException;
import com.younggeun.delivery.global.exception.impl.PasswordMismatchException;
import com.younggeun.delivery.global.exception.impl.UserNotFoundException;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    if(this.userRepository.existsByEmail(user.getEmail())) {
      throw new AlreadyExistUserException();
    }

    if(this.userRepository.existsByNickname(user.getNickname())) {
      throw new AlreadyExistUserException();
    }

    user.setPassword(this.passwordEncoder.encode(user.getPassword()));


    return this.userRepository.save(user.toEntity());
  }

  // 로그인
  public User authenticate(Auth.SignIn user) {
    var member = this.userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);

    if(!this.passwordEncoder.matches(user.getPassword(), member.getPassword())) {
      throw new PasswordMismatchException();
    }
    return member;
  }

}
