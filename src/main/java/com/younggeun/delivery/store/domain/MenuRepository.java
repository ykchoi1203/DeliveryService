package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<Menu, Long> {
  @Query(nativeQuery = true, value = "SELECT * FROM MENU WHERE category_id IN (:categoryId)")
  List<Menu> findAllByMenuCategoryId(@Param("categoryId") List<Long> menuCategoryIdList);

  boolean existsByMenuCategory(MenuCategory category);
}
