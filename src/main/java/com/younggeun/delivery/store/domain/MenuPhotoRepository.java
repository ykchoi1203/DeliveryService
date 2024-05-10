package com.younggeun.delivery.store.domain;

import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuPhoto;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuPhotoRepository extends JpaRepository<MenuPhoto, Long> {
  Optional<MenuPhoto> findByMenu(Menu menu);

  boolean existsByMenu(Menu menu);
}
