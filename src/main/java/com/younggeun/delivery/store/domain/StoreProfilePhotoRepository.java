package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StoreProfilePhoto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreProfilePhotoRepository extends JpaRepository<StoreProfilePhoto, Long> {

  Optional<StoreProfilePhoto> findByStore(Store store);

  boolean existsByStore(Store store);
}
