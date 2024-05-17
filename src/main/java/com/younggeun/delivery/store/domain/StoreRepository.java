package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.partner.domain.entity.Partner;
import com.younggeun.delivery.store.domain.entity.Store;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

  Page<Store> findAllByPartner(Partner partner, Pageable pageable);

  @Query(value = "SELECT (store.total_stars / (CASE WHEN store.total_reviews = 0 THEN 1 ELSE store.total_reviews END)) AS star_rate, store.* "
      + "FROM store WHERE store_id IN :storeId ORDER BY star_rate ASC ", nativeQuery = true)
  List<Store> findAllByStoreIdInOrderByStar(List<Long> storeId);

  @Query(value = "SELECT (store.total_stars / (CASE WHEN store.total_reviews = 0 THEN 1 ELSE store.total_reviews END)) AS star_rate, store.* "
      + "FROM store WHERE store_id IN :storeId ORDER BY star_rate DESC", nativeQuery = true)
  List<Store> findAllByStoreIdInOrderByStarDesc(List<Long> storeId);

  @Query(value = "SELECT ROUND(SQRT(POWER(111.045 * (CAST(store.latitude AS REAL) - :lat), 2) + POWER(111.045 * (:lnt - CAST(store.longitude AS REAL)) * COS(CAST(store.latitude AS REAL) / 57.3), 2)), 6) AS distance_difference, store.* "
      + "FROM store WHERE store_id IN :storeId ORDER BY distance_difference ASC ", nativeQuery = true)
  List<Store> findAllByStoreIdInByDist(List<Long> storeId, double lat, double lnt);

  @Query(value = "SELECT ROUND(SQRT(POWER(111.045 * (CAST(store.latitude AS REAL) - :lat), 2) + POWER(111.045 * (:lnt - CAST(store.longitude AS REAL)) * COS(CAST(store.latitude AS REAL) / 57.3), 2)), 6) AS distance_difference, store.* "
      + "FROM store WHERE store_id IN :storeId ORDER BY distance_difference DESC ", nativeQuery = true)
  List<Store> findAllByStoreIdInByDistDesc(List<Long> storeId, double lat, double lnt);

  List<Store> findAllByStoreIdInOrderByDeliveryCostAsc(List<Long> storeDocumentIdList);
  List<Store> findAllByStoreIdInOrderByDeliveryCostDesc(List<Long> storeDocumentIdList);

}
