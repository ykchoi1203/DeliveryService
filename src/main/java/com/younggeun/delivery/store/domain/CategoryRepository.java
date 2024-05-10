package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  List<Category> findAllByOrderBySequenceAsc();
  boolean existsBySequence(int idx);
}
