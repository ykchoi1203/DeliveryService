package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, Long> {

  List<DeliveryAddress> findAllByUser(User user);

  Optional<DeliveryAddress> findByUserAndDefaultAddressStatusIsTrue(User user);

  Optional<DeliveryAddress> findByUserOrderByUpdatedAtDesc(User user);
}
