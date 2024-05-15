package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderStatus;
import com.younggeun.delivery.user.domain.entity.OrderTable;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderTable, Long> {

  Page<OrderTable> findAllByUserUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

  Page<OrderTable> findAllByStoreAndStatusGreaterThanAndUpdatedAtBetweenOrderByUpdatedAtDesc(Store store,
      OrderStatus status, LocalDateTime openTime, LocalDateTime closeTime, Pageable pageable);
}
