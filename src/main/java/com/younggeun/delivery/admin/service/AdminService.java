package com.younggeun.delivery.admin.service;

import static com.younggeun.delivery.global.entity.RoleType.ROLE_ADMIN;
import static com.younggeun.delivery.global.exception.type.CommonErrorCode.CATEGORY_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.CommonErrorCode.EXIST_CATEGORY_NAME;
import static com.younggeun.delivery.global.exception.type.CommonErrorCode.NOT_ALLOW_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.EXISTS_SEQUENCE_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_ACCESS_STATUS_IS_SAME;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_DOCUMENT_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_PASSWORD_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.global.model.Auth;
import com.younggeun.delivery.store.domain.CategoryRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.documents.StoreDocument;
import com.younggeun.delivery.store.domain.documents.repository.StoreDocumentRepository;
import com.younggeun.delivery.store.domain.dto.CategoryDto;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.entity.Category;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
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
  private final StoreRepository storeRepository;
  private final ElasticsearchRestTemplate elasticsearchRestTemplate;
  private final StoreDocumentRepository storeDocumentRepository;

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

    if(member.getRole() != ROLE_ADMIN) {
      throw new RestApiException(NOT_ALLOW_EXCEPTION);
    }

    return member;
  }

  public List<Category> getCategory() {
    return categoryRepository.findAllByOrderBySequenceAsc();
  }

  public Category createCategory(CategoryDto categoryDto) {
    if(existIdx(categoryDto.getSequence())) throw new RestApiException(EXISTS_SEQUENCE_EXCEPTION);

    if(categoryRepository.existsByName(categoryDto.getName())) throw new RestApiException(EXIST_CATEGORY_NAME);

    return categoryRepository.save(categoryDto.toEntity(categoryDto.getCategoryId()));
  }

  public Category updateCategory(CategoryDto categoryDto, Long categoryId) {
    Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new RestApiException(CATEGORY_NOT_FOUND));
    if(existIdx(categoryDto.getSequence())) throw new RestApiException(EXISTS_SEQUENCE_EXCEPTION);
    category.setName(categoryDto.getName());
    category.setSequence(categoryDto.getSequence());
    return categoryRepository.save(category);
  }

  public boolean existIdx(int idx) {
    return categoryRepository.existsBySequence(idx);
  }

  @Transactional
  public boolean deleteCategory(long categoryId) {
    categoryRepository.deleteById(categoryId);
    return true;
  }

  public Page<StoreDto> getStoreList(Pageable pageable) {
    return storeRepository.findAllByOrderByCreatedAtDesc(pageable).map(StoreDto::new);
  }

  public StoreDto getStore(String storeId) {
    return new StoreDto(storeRepository.findById(Long.parseLong(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND)));
  }

  @Transactional
  public StoreDto changeStoreStatus(String storeId, boolean status) {
    Store store = storeRepository.findById(Long.parseLong(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));

    if(store.isAccessStatus() == status) {
      throw new RestApiException(STORE_ACCESS_STATUS_IS_SAME);
    }

    store.setAccessStatus(status);

    store = storeRepository.save(store);

    if(status) {
      elasticsearchRestTemplate.save(new StoreDocument(store));
    } else {
      StoreDocument storeDocument = storeDocumentRepository.findById(store.getStoreId()).orElseThrow(() -> new RestApiException(STORE_DOCUMENT_NOT_FOUND));
      elasticsearchRestTemplate.delete(storeDocument);
    }

    return new StoreDto(store);
  }
}
