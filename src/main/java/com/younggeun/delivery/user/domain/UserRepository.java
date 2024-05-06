package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailAndDeletedAtIsNull(String email);

  boolean existsByEmailAndDeletedAtIsNull(String email);

  boolean existsByNicknameAndDeletedAtIsNull(String nickname);

  boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
}
