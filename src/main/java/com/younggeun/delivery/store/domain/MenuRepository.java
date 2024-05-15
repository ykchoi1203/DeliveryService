package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MenuRepository extends JpaRepository<Menu, Long> {
  List<Menu> findAllByMenuCategoryCategoryIdIn(List<Long> menuCategoryIdList);

  @Query("SELECT NEW com.younggeun.delivery.store.domain.dto.MenuDto(m.menuId, m.menuName, m.price, m.description, m.soldOutStatus, mc.categoryId) " +
      "FROM Menu m " +
      "LEFT JOIN m.menuCategory mc " +
      "WHERE mc.store.storeId = :storeId")
  List<Menu> findAllMenusWithDetailsByStoreId(@Param("storeId") String storeId);

  boolean existsByMenuCategory(MenuCategory category);

  boolean existsByMenuIdIn(List<Long> menuIdList);

  List<Menu> findAllByMenuIdIn(List<Long> menuIdList);
}
