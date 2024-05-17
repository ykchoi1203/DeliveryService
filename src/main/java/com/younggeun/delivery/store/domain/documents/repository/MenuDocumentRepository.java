package com.younggeun.delivery.store.domain.documents.repository;

import com.younggeun.delivery.store.domain.documents.MenuDocument;
import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface MenuDocumentRepository extends ElasticsearchRepository<MenuDocument, Long> {

  List<MenuDocument> findByNameContaining(String query);
}
