package com.younggeun.delivery.user.domain;

import com.younggeun.delivery.user.domain.entity.User;
import com.younggeun.delivery.user.domain.entity.Wish;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

  List<Wish> findAllByUser(User user);
}
