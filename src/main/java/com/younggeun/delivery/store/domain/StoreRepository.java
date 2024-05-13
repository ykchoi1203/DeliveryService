package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.entity.Category;
import com.younggeun.delivery.store.domain.entity.Store;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

  Page<Store> findAllByPartner(Partner partner, Pageable pageable);

  Page<Store> findAllByAddress2(String address2, Pageable pageable);

  List<Store> findAllByAddress2AndCategory(String address2, Category category);

  List<Store> findAllByAddress2AndCategoryOrderByDeliveryCost(String address2, Category category);

  @Query(value = "SELECT ROUND(SQRT(POWER(111.045 * (CAST(store.latitude AS REAL) - :lat), 2) + POWER(111.045 * (:lnt - CAST(store.longitude AS REAL)) * COS(CAST(store.latitude AS REAL) / 57.3), 2)), 6) AS distance_difference, store.* "
      + "FROM store WHERE category_id = :categoryId AND address2 = :address2 ORDER BY distance_difference", nativeQuery = true)
  List<Store> findAllByCategoryOrderByDist(String address2, Long categoryId, double lat, double lnt);

  @Query(value = "SELECT (store.total_stars / (CASE WHEN store.total_reviews = 0 THEN 1 ELSE store.total_reviews END)) AS star_rate, store.* "
      + "FROM store WHERE category_id = :categoryId AND address2 = :address2 ORDER BY star_rate", nativeQuery = true)
  List<Store> findAllByCategoryOrderByStar(String address2, Long categoryId);

  List<Store> findAllByAddress2OrderByDeliveryCost(String address2);

  @Query(value = "SELECT (store.total_stars / (CASE WHEN store.total_reviews = 0 THEN 1 ELSE store.total_reviews END)) AS star_rate, store.* "
      + "FROM store WHERE address2 = :address2 ORDER BY star_rate", nativeQuery = true)
  List<Store> findAllByAddress2OrderByStar(String address2);

  @Query(value = "SELECT ROUND(SQRT(POWER(111.045 * (CAST(store.latitude AS REAL) - :lat), 2) + POWER(111.045 * (:lnt - CAST(store.longitude AS REAL)) * COS(CAST(store.latitude AS REAL) / 57.3), 2)), 6) AS distance_difference, store.* "
      + "FROM store WHERE address2 = :address2 ORDER BY distance_difference", nativeQuery = true)
  List<Store> findAllByAddress2OrderByDist(String address2, double lat, double lnt);
}
