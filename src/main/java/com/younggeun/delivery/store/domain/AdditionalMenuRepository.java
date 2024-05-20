package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.store.domain.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdditionalMenuRepository extends JpaRepository<AdditionalMenu, Long> {

  boolean existsBySequenceAndMenu(int sequence, Menu menu);

  List<AdditionalMenu> findAllByMenuMenuIdIn(List<Long> menuIdList);

  List<AdditionalMenu> findAllByMenu(Menu menu);

  List<AdditionalMenu> findAllByAdditionalMenuIdIn(List<Long> additionalIdList);

  boolean existsByAdditionalMenuIdIn(List<Long> additionalMenuIdList);

  boolean existsByMenuMenuCategoryStoreStoreIdAndSoldOutStatusIsTrue(long storeId);

  List<AdditionalMenu> findAllByMenuMenuCategoryStoreStoreIdAndSoldOutStatusIsTrue(long storeId);
}
