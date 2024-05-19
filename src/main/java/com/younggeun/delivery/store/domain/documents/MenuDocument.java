package com.younggeun.delivery.store.domain.documents;

import com.younggeun.delivery.store.domain.entity.Menu;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "menu")
@Setting(settingPath = "elasticsearch/setting.json")
@Mapping(mappingPath = "elasticsearch/mapping.json")
public class MenuDocument {
  @Id
  private Long id;
  private String name;
  private Long storeId;

  public MenuDocument(Menu menu , Long storeId) {
    this.id = menu.getMenuId();
    this.name = menu.getMenuName();
    this.storeId = storeId;
  }
}
