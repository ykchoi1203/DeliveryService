package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {

  Page<Store> findAllByPartner(Partner partner, Pageable pageable);
}
