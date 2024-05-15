package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.OrderHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long> {

  List<OrderHistory> findAllByOrderTableOrderId(Long orderId);

}
