package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
