package com.younggeun.delivery.partner.domain;

import com.younggeun.delivery.partner.domain.entity.Partner;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

  boolean existsByEmail(String email);

  Optional<Partner> findByEmailAndDeletedAtIsNull(String email);

  boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber);
}
