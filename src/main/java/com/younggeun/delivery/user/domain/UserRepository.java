package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.type.AuthType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  boolean existsByPhoneNumber(String phoneNumber);

  boolean existsByEmail(String email);

  boolean existsByNickname(String nickname);

  Optional<User> findByEmail(String email);

  Optional<User> findByProvideIdAndAuthType(String sub, AuthType authType);
}
