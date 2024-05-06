package com.younggeun.delivery.admin.service;

import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.global.exception.impl.PasswordMismatchException;
import com.younggeun.delivery.global.exception.impl.UserNotFoundException;
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
        .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

    if(!user.getRole().equals(RoleType.ROLE_ADMIN)) {
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
    var member = userRepository.findByEmail(user.getEmail()).orElseThrow(UserNotFoundException::new);

    if(!passwordEncoder.matches(user.getPassword(), member.getPassword())) {
      throw new PasswordMismatchException();
    }

    if(!member.getRole().equals(RoleType.ROLE_ADMIN)) {
      throw new RuntimeException();
    }

    return member;
  }

  public List<Category> getCategory() {
    return categoryRepository.findAll();
  }

  public Category createCategory(CategoryDto categoryDto) {
    return categoryRepository.save(categoryDto.toEntity());
  }

  public Category updateCategory(CategoryDto categoryDto, Long categoryId) {
    return categoryRepository.save(Category.builder()
                                            .categoryId(categoryId)
                                            .name(categoryDto.getName())
                                            .sequence(categoryDto.getSequence())
                                            .build());

  }

  @Transactional
  public boolean deleteCategory(long categoryId) {

    categoryRepository.deleteById(categoryId);
    return true;
  }
}
