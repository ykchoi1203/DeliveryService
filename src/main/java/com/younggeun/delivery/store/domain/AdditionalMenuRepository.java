package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.store.domain.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AdditionalMenuRepository extends JpaRepository<AdditionalMenu, Long> {

  boolean existsBySequenceAndMenu(int sequence, Menu menu);

  @Query(nativeQuery = true, value = "SELECT * FROM additional_menu WHERE menu_id IN (:menuId)")
  List<AdditionalMenu> findAllByMenuId(@Param("menuId") List<Long> menuIdList);

  List<AdditionalMenu> findAllByMenu(Menu menu);
}
