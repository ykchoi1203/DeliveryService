package com.younggeun.delivery.store.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Entity
@Where(clause = "deleted_at is null")
public class StoreProfilePhoto extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long storeProfilePhotoId;

  private String url;
  private String photoName;

  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "store_id")
  private Store store;
}
