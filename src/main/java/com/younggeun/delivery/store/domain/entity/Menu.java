package com.younggeun.delivery.store.domain.entity;

import com.younggeun.delivery.global.entity.BaseEntity;
import jakarta.persistence.Column;
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
public class Menu extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long menuId;

  @Column(nullable = false)
  private String menuName;

  @Column(nullable = false, columnDefinition = "INT CHECK (price > 0)")
  private int price;
  private String description;

  @Column(nullable = false)
  private boolean soldOutStatus;
  private LocalDateTime deletedAt;

  @ManyToOne
  @JoinColumn(name = "category_id", nullable = false)
  private MenuCategory menuCategory;

}
