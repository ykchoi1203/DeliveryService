package com.younggeun.delivery.store.domain.documents;

import com.younggeun.delivery.store.domain.entity.Store;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "store")
public class StoreDocument {
  @Id
  private Long id;
  private String name;
  private Long categoryId;
  private String address1;
  private String address2;
  private String address3;


  public StoreDocument(Store store) {
    this.id = store.getStoreId();
    this.name = store.getStoreName();
    this.address1 = store.getAddress1();
    this.address2 = store.getAddress2();
    this.address3 = store.getAddress3();
    this.categoryId = store.getCategory().getCategoryId();
  }
}
