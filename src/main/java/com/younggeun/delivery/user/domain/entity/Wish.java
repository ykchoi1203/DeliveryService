package com.younggeun.delivery.user.domain.entity;

import com.younggeun.delivery.store.domain.entity.Store;
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
import org.springframework.data.annotation.CreatedDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Builder
public class Wish {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long wishId;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @CreatedDate
  private LocalDateTime createdAt;

  public Wish(User user, Store store) {
    this.user = user;
    this.store = store;
  }
}
