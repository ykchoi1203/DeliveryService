package com.younggeun.delivery.store.service;

import static com.younggeun.delivery.global.exception.type.StoreErrorCode.ALREADY_EXIST_SEQUENCE;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MENU_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_STORE_CATEGORY;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_CATEGORY_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.MenuCategoryRepository;
import com.younggeun.delivery.store.domain.MenuRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.dto.MenuCategoryDto;
import com.younggeun.delivery.store.domain.dto.MenuDto;
import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuCategory;
import com.younggeun.delivery.store.domain.entity.Store;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MenuService {

  private final StoreRepository storeRepository;
  private final MenuRepository menuRepository;
  private final MenuCategoryRepository menuCategoryRepository;

  public List<Menu> selectMenu(Authentication authentication, String storeId) {
    Store store = isMatchUser(authentication, storeId);

    List<MenuCategory> menuCategoryList = menuCategoryRepository.findAllByStore(store);
    List<Long> menuCategoryIdList = menuCategoryList.stream().map(MenuCategory::getCategoryId).toList();

    return menuRepository.findAllByMenuCategoryId(menuCategoryIdList);
  }

  public MenuCategory createStoreCategory(Authentication authentication, MenuCategoryDto menuCategoryDto, String storeId) {
    Store store = isMatchUser(authentication, storeId);

    if(menuCategoryRepository.existsBySequence(store.getStoreId(), menuCategoryDto.getSequence())) {
      throw new RestApiException(ALREADY_EXIST_SEQUENCE);
    }


    return menuCategoryRepository.save(MenuCategory.builder()
                                                    .sequence(menuCategoryDto.getSequence())
                                                    .name(menuCategoryDto.getName())
                                                    .store(store)
                                                    .build());
  }

  public Menu createStoreMenu(Authentication authentication, MenuDto menuDto, String storeId) {

    isMatchUser(authentication, storeId);
    MenuCategory menuCategory = menuCategoryRepository.findById(menuDto.getCategoryId()).orElseThrow(() -> new RestApiException(STORE_CATEGORY_NOT_FOUND));

    if(menuCategory.getStore().getStoreId() != Long.parseLong(storeId)) {
      throw new RestApiException(MISMATCH_STORE_CATEGORY);
    }

    return menuRepository.save(Menu.builder()
                                    .menuName(menuDto.getMenuName())
                                    .price(menuDto.getPrice())
                                    .description(menuDto.getDescription())
                                    .soldOutStatus(false)
                                    .menuCategory(menuCategory).build());
  }

  public Menu updateStoreMenu(Authentication authentication, MenuDto menuDto, String storeId) {
    isMatchUser(authentication, storeId);
    Menu menu = menuRepository.findById(menuDto.getMenuId()).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));
    MenuCategory menuCategory = menuCategoryRepository.findById(menuDto.getCategoryId()).orElseThrow(() -> new RestApiException(STORE_CATEGORY_NOT_FOUND));
    return menuRepository.save(Menu.builder()
                                      .menuId(menu.getMenuId())
                                      .menuName(menuDto.getMenuName())
                                      .price(menuDto.getPrice())
                                      .description(menuDto.getDescription())
                                      .soldOutStatus(menuDto.isSoldOutStatus())
                                      .menuCategory(menuCategory)
                                      .build());
  }

  public boolean deleteStoreMenu(Authentication authentication, long menuId, String storeId) {
    isMatchUser(authentication, storeId);

    menuRepository.findById(menuId).ifPresent(item -> {
      item.setDeletedAt(LocalDateTime.now());
      menuRepository.save(item);
    });

    return true;
  }

  private Store isMatchUser(Authentication authentication, String storeId) {
    Store store = storeRepository.findById(Long.parseLong(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));
    if(!store.getPartner().getEmail().equals(authentication.getName())) {
      throw new RestApiException(MISMATCH_USER_EXCEPTION);
    }
    return store;
  }



}
