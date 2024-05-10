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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Where(clause = "deleted_at is null")
public class AdditionalMenu extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long additionalMenuId;

  private String menuName;

  private int price;
  private int sequence;

  private boolean soldOutStatus;
  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "menu_id")
  private Menu menu;
}
