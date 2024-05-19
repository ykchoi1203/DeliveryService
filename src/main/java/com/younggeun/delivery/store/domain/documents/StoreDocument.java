package com.younggeun.delivery.store.domain.documents;

import com.younggeun.delivery.store.domain.entity.Store;
import jakarta.persistence.Id;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Document(indexName = "store")
@Setting(settingPath = "elasticsearch/setting.json")
@Mapping(mappingPath = "elasticsearch/mapping.json")
public class StoreDocument {
  @Id
  private Long id;
  private String name;
  private Long categoryId;
  private String address1;
  private String address2;
  private String address3;
  private int deliveryCost;
  private double stars;

  @GeoPointField
  private GeoPoint location;

  private List<MenuDocument> menuDocumentList;

  public StoreDocument(Store store) {
    this.id = store.getStoreId();
    this.name = store.getStoreName();
    this.address1 = store.getAddress1();
    this.address2 = store.getAddress2();
    this.address3 = store.getAddress3();
    this.categoryId = store.getCategory().getCategoryId();
    this.deliveryCost = store.getDeliveryCost();
    this.stars = store.getTotalStars() / (double)(store.getTotalReviews() != 0 ? store.getTotalReviews() : 1);
    this.location = new GeoPoint(store.getLatitude(), store.getLongitude());
  }

  public StoreDocument(Store store, List<MenuDocument> menuList) {
    this.id = store.getStoreId();
    this.name = store.getStoreName();
    this.address1 = store.getAddress1();
    this.address2 = store.getAddress2();
    this.address3 = store.getAddress3();
    this.categoryId = store.getCategory().getCategoryId();
    this.location = new GeoPoint(store.getLatitude(), store.getLongitude());
    this.menuDocumentList = menuList;
  }
}
