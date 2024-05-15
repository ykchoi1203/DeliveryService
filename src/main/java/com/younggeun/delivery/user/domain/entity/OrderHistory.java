package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.store.domain.entity.Menu;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedEntityGraph;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

@AllArgsConstructor
@NoArgsConstructor
@NamedEntityGraph
@Builder
@Getter
@Entity
public class OrderHistory {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long OrderHistoryId;

  private int quantity;

  @CreatedDate
  private LocalDateTime createdAt;

  @ManyToOne
  @JoinColumn(name = "order_id")
  private OrderTable orderTable;

  @ManyToOne
  @JoinColumn(name = "menu_id")
  private Menu menu;

  @ManyToOne
  @JoinColumn(name = "additional_menu_id")
  private AdditionalMenu additionalMenu;

  public OrderHistory(OrderTable orderTable, Menu menu, int quantity) {
    this.orderTable = orderTable;
    this.menu = menu;
    this.quantity = quantity;
  }

  public OrderHistory(OrderTable orderTable, AdditionalMenu additionalMenu, int quantity) {
    this.orderTable = orderTable;
    this.additionalMenu = additionalMenu;
    this.quantity = quantity;
  }
}
