package com.younggeun.delivery.store.domain.documents.repository;

import com.younggeun.delivery.store.domain.documents.MenuDocument;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MenuDocumentRepository extends ElasticsearchRepository<MenuDocument, Long> {

  Page<MenuDocument> findByNameContaining(String query, Pageable page);

  List<MenuDocument> findByStoreId(Long storeId);

  MenuDocument findById(long menuId);
}
