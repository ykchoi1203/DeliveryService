package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.MenuCategory;
import com.younggeun.delivery.store.domain.entity.Store;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory, Long> {

  List<MenuCategory> findAllByStoreOrderBySequence(Store store);

  @Query(nativeQuery = true, value = "select EXISTS (select category_id from menu_category where store_id=(:storeId) and sequence = (:sequence) limit 1) as success")
  Long existsBySequence(@Param("storeId") Long storeId,@Param("sequence") int sequence);

  List<MenuCategory> findAllByStore(Store store);
}
