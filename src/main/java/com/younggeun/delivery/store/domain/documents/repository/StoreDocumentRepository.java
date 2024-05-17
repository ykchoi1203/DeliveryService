package com.younggeun.delivery.store.domain.documents.repository;

import com.younggeun.delivery.store.domain.documents.StoreDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StoreDocumentRepository extends ElasticsearchRepository<StoreDocument, Long> {

  Page<StoreDocument> findByAddress1AndAddress2(String address1, String address2, Pageable pageable);

  Page<StoreDocument> findByAddress1AndAddress2AndNameContaining(String address1, String address2, String query,
      Pageable pageable);

  Page<StoreDocument> findByAddress1AndAddress2AndIdIn(String address1, String address2, List<Long> storeIdByMenuCategory, Pageable pageable);

  Page<StoreDocument> findByCategoryIdAndAddress1AndAddress2AndNameContaining(long categoryId, String address1, String address2,
      String query, Pageable page);

  Page<StoreDocument> findByCategoryIdAndAddress1AndAddress2AndIdIn(long categoryId, String address1, String address2, List<Long> storeIdByMenuCategory,
      Pageable pageable);
}
