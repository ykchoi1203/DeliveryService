package com.younggeun.delivery.store.domain.documents.repository;

import com.younggeun.delivery.store.domain.documents.StoreDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface StoreDocumentRepository extends ElasticsearchRepository<StoreDocument, Long> {

  List<StoreDocument> findByAddress1AndAddress2(String address1, String address2);

  List<StoreDocument> findByCategoryIdAndAddress1AndAddress2(Long categoryId, String address1, String address2);

  List<StoreDocument> findByNameContaining(String query);

  List<StoreDocument> findByAddress1AndAddress2AndNameContaining(String address1, String address2, String query);

  List<StoreDocument> findByAddress1AndAddress2AndIdIn(String address1, String address2, List<Long> storeIdByMenuCategory);

  List<StoreDocument> findByCategoryIdAndAddress1AndAddress2AndNameContaining(long categoryId, String address1, String address2,
      String query);

  List<StoreDocument> findByCategoryIdAndAddress1AndAddress2AndIdIn(long categoryId, String address1, String address2, List<Long> storeIdByMenuCategory);
}
