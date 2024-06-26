package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StorePhoto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorePhotoRepository extends JpaRepository<StorePhoto, Long> {

  Optional<StorePhoto> findByStore(Store store);

  boolean existsByStore(Store store);

  List<StorePhoto> findAllByStoreStoreIdIn(List<Long> storeIds);
}
