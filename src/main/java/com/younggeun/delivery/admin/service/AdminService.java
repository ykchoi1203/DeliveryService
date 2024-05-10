package com.younggeun.delivery.admin.service;

import static com.younggeun.delivery.global.entity.RoleType.ROLE_ADMIN;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.EXISTS_SEQUENCE_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_PASSWORD_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.store.domain.CategoryRepository;
import com.younggeun.delivery.store.domain.dto.CategoryDto;
import com.younggeun.delivery.store.domain.entity.Category;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class AdminService implements UserDetailsService {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!user.getRole().equals(ROLE_ADMIN)) {
      throw new RuntimeException();
    }

    return org.springframework.security.core.userdetails.User
        .withUsername(email)
        .password(user.getPassword())
        .roles("ADMIN")
        .build();
  }

  // 로그인
  public User authenticate(Auth.SignIn user) {
    var member = userRepository.findByEmail(user.getEmail()).orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));

    if(!passwordEncoder.matches(user.getPassword(), member.getPassword())) {
      throw new RestApiException(MISMATCH_PASSWORD_EXCEPTION);
    }

    if(member.getRole() == ROLE_ADMIN) {
      throw new RuntimeException();
    }

    return member;
  }

  public List<Category> getCategory() {
    return categoryRepository.findAllByOrderBySequenceAsc();
  }

  public Category createCategory(CategoryDto categoryDto) {
    if(existIdx(categoryDto.getSequence())) throw new RestApiException(EXISTS_SEQUENCE_EXCEPTION);
    return categoryRepository.save(categoryDto.toEntity());
  }

  public Category updateCategory(CategoryDto categoryDto, Long categoryId) {
    if(existIdx(categoryDto.getSequence())) throw new RestApiException(EXISTS_SEQUENCE_EXCEPTION);

    return categoryRepository.save(categoryDto.toEntity(categoryId));
  }

  public boolean existIdx(int idx) {
    return categoryRepository.existsBySequence(idx);
  }

  @Transactional
  public boolean deleteCategory(long categoryId) {
    categoryRepository.deleteById(categoryId);
    return true;
  }
}
