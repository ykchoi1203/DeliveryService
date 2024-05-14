package com.younggeun.delivery.store.service;

import static com.younggeun.delivery.global.exception.type.StoreErrorCode.ADDITIONAL_MENU_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.ALREADY_EXIST_SEQUENCE;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.CANNOT_DELETE_CATEGORY_CAUSE_EXIST_MENU_BY_CATEGORY_ID;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.EXISTS_SEQUENCE_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MENU_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_PARTNER_STORE;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_STORE_CATEGORY;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.MISMATCH_STORE_MENU;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_CATEGORY_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.StoreErrorCode.STORE_NOT_FOUND;

import com.younggeun.delivery.global.entity.RoleType;
import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.AdditionalMenuRepository;
import com.younggeun.delivery.store.domain.MenuCategoryRepository;
import com.younggeun.delivery.store.domain.MenuPhotoRepository;
import com.younggeun.delivery.store.domain.MenuRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.dto.AdditionalMenuDto;
import com.younggeun.delivery.store.domain.dto.MenuCategoryDto;
import com.younggeun.delivery.store.domain.dto.MenuDto;
import com.younggeun.delivery.store.domain.dto.MenuListDto;
import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.store.domain.entity.AdditionalMenu;
import com.younggeun.delivery.store.domain.entity.Menu;
import com.younggeun.delivery.store.domain.entity.MenuCategory;
import com.younggeun.delivery.store.domain.entity.MenuPhoto;
import com.younggeun.delivery.store.domain.entity.Store;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@AllArgsConstructor
public class MenuService {

  private final StoreRepository storeRepository;
  private final MenuRepository menuRepository;
  private final MenuCategoryRepository menuCategoryRepository;
  private final MenuPhotoRepository menuPhotoRepository;
  private final AdditionalMenuRepository additionalMenuRepository;

  public List<MenuListDto> selectMenu(Authentication authentication, String storeId, RoleType type) {
    Store store = type == RoleType.ROLE_PARTNER ? getStoreWithMatchUser(authentication, storeId) : getStore(storeId);

    // 메뉴 카테고리 및 관련 메뉴를 한 번에 가져오기
    List<MenuCategory> menuCategoryList = menuCategoryRepository.findAllByStoreOrderBySequence(store);
    List<MenuListDto> menuListDtos = menuCategoryList.stream().map(MenuListDto::new).collect(Collectors.toList());

    Map<Long, MenuListDto> menuListDtoMap = menuListDtos.stream().collect(Collectors.toMap(MenuListDto::getCategoryId, Function.identity()));

    // 카테고리별로 메뉴를 가져오고 DTO로 변환
    List<Menu> menuList = menuRepository.findAllByMenuCategoryCategoryIdIn(menuCategoryList.stream().map(MenuCategory::getCategoryId).collect(Collectors.toList()));
    Map<Long, List<Menu>> menuMap = menuList.stream().collect(Collectors.groupingBy(menu -> menu.getMenuCategory().getCategoryId()));

    List<Long> menuIdList = menuList.stream().map(Menu::getMenuId).collect(Collectors.toList());

    // 메뉴 사진을 가져오고 메뉴 ID를 키로하여 매핑
    Map<Long, PhotoDto> menuPhotoMap = menuPhotoRepository.findAllByMenuMenuIdIn(menuIdList).stream()
        .collect(Collectors.toMap(photo -> photo.getMenu().getMenuId(), PhotoDto::new));

    // 메뉴 DTO에 메뉴 사진 및 추가 메뉴 매핑
    for (MenuCategory menuCategory : menuCategoryList) {
      MenuListDto menuListDto = menuListDtoMap.get(menuCategory.getCategoryId());
      List<MenuDto> menuDtoList = menuMap.getOrDefault(menuCategory.getCategoryId(), Collections.emptyList()).stream()
          .map(menu -> {
            MenuDto menuDto = new MenuDto(menu);
            menuDto.setMenuPhoto(menuPhotoMap.get(menu.getMenuId()));
            return menuDto;
          })
          .collect(Collectors.toList());
      menuListDto.setMenuList(menuDtoList);
    }

    return menuListDtos;
  }

  public MenuCategory createStoreCategory(Authentication authentication, MenuCategoryDto menuCategoryDto, String storeId) {
    Store store = getStoreWithMatchUser(authentication, storeId);

    if(menuCategoryRepository.existsBySequence(store.getStoreId(), menuCategoryDto.getSequence()) > 0) {
      throw new RestApiException(ALREADY_EXIST_SEQUENCE);
    }

    return menuCategoryRepository.save(menuCategoryDto.toEntity(store));
  }

  public Menu createStoreMenu(Authentication authentication, MenuDto menuDto, String storeId) {

    getStoreWithMatchUser(authentication, storeId);
    MenuCategory menuCategory = menuCategoryRepository.findById(menuDto.getCategoryId()).orElseThrow(() -> new RestApiException(STORE_CATEGORY_NOT_FOUND));

    if(menuCategory.getStore().getStoreId() != Long.parseLong(storeId)) {
      throw new RestApiException(MISMATCH_STORE_CATEGORY);
    }

    return menuRepository.save(menuDto.toEntity(menuCategory));
  }

  public Menu updateStoreMenu(Authentication authentication, MenuDto menuDto, String storeId) {
    getStoreWithMatchUser(authentication, storeId);
    Menu menu = menuRepository.findById(menuDto.getMenuId()).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));
    MenuCategory menuCategory = menuCategoryRepository.findById(menuDto.getCategoryId()).orElseThrow(() -> new RestApiException(STORE_CATEGORY_NOT_FOUND));
    return menuRepository.save(menuDto.toEntity(menu.getMenuId(), menuCategory));
  }

  public boolean deleteStoreMenu(Authentication authentication, String storeId, long menuId) {
    getStoreWithMatchUser(authentication, storeId);

    menuRepository.findById(menuId).ifPresent(item -> {
      item.setDeletedAt(LocalDateTime.now());
      menuRepository.save(item);
    });

    return true;
  }

  private Store getStoreWithMatchUser(Authentication authentication, String storeId) {
    Store store = storeRepository.findById(Long.parseLong(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));
    if(!store.getPartner().getEmail().equals(authentication.getName())) {
      throw new RestApiException(MISMATCH_PARTNER_STORE);
    }
    return store;
  }

  private Store getStore(String storeId) {
    return storeRepository.findById(Long.parseLong(storeId)).orElseThrow(() -> new RestApiException(STORE_NOT_FOUND));
  }


  public List<MenuCategory> selectMenuCategory(Authentication authentication, String storeId) {
    Store store = getStoreWithMatchUser(authentication, storeId);

    return menuCategoryRepository.findAllByStore(store);
  }

  public MenuCategory updateStoreMenuCategory(Authentication authentication, MenuCategoryDto menuCategoryDto, String storeId, String categoryId) {
    Store store = getStoreWithMatchUser(authentication, storeId);
    return menuCategoryRepository.save(menuCategoryDto.toEntity(store, Long.parseLong(categoryId)));
  }

  public boolean deleteStoreMenuCategory(Authentication authentication, String storeId, long categoryId) {
    getStoreWithMatchUser(authentication, storeId);

    MenuCategory category = menuCategoryRepository.findById(categoryId).orElseThrow(() -> new RestApiException(STORE_CATEGORY_NOT_FOUND));

    if(menuRepository.existsByMenuCategory(category))
      throw new RestApiException(CANNOT_DELETE_CATEGORY_CAUSE_EXIST_MENU_BY_CATEGORY_ID);

    menuCategoryRepository.delete(category);

    return true;
  }

  @Transactional
  public MenuPhoto createMenuPhoto(Authentication authentication, MultipartFile file, String storeId, long menuId, String menuBaseLocalPath, String menuBaseUrlPath) {
    getStoreWithMatchUser(authentication, storeId);

    PhotoDto menuPhotoDto = new PhotoDto();
    menuPhotoDto.savePhotos(file, menuBaseLocalPath, menuBaseUrlPath);

    Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));

    menuPhotoDto.setMenu(menu);

    return menuPhotoRepository.save(menuPhotoDto.toMenuPhotoEntity());

  }

  @Transactional
  public MenuPhoto updateMenuPhoto(Authentication authentication, MultipartFile file, String storeId, long menuId, String menuBaseLocalPath, String menuBaseUrlPath) {
    getStoreWithMatchUser(authentication, storeId);
    Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));

    menuPhotoRepository.findByMenu(menu).ifPresent(menuPhoto -> {
      menuPhoto.setDeletedAt(LocalDateTime.now());
      menuPhotoRepository.save(menuPhoto);
    });

    PhotoDto menuPhotoDto = new PhotoDto();
    menuPhotoDto.savePhotos(file, menuBaseLocalPath, menuBaseUrlPath);

    menuPhotoDto.setMenu(menu);

    return menuPhotoRepository.save(menuPhotoDto.toMenuPhotoEntity());

  }

  public AdditionalMenu createAdditionalMenu(Authentication authentication,
      AdditionalMenuDto additionalMenuDto, String storeId, String menuId) {
    Store store = getStoreWithMatchUser(authentication, storeId);
    Menu menu = isMatchStore(store, menuId);
    if(additionalMenuRepository.existsBySequenceAndMenu(additionalMenuDto.getSequence(), menu)) {
      throw new RestApiException(EXISTS_SEQUENCE_EXCEPTION);
    }

    return additionalMenuRepository.save(additionalMenuDto.toEntity(menu));
  }

  public AdditionalMenu updateAdditionalMenu(Authentication authentication, AdditionalMenuDto additionalMenuDto, String storeId) {
    Store store = getStoreWithMatchUser(authentication, storeId);
    Menu menu = isMatchStore(store, additionalMenuDto.getMenuId().toString());
    AdditionalMenu additionalMenu = additionalMenuRepository.findById(additionalMenuDto.getAdditionalMenuId()).orElseThrow(() -> new RestApiException(ADDITIONAL_MENU_NOT_FOUND));
    if(additionalMenu.getSequence() != additionalMenuDto.getSequence() && additionalMenuRepository.existsBySequenceAndMenu(additionalMenuDto.getSequence(), menu)) {
      throw new RestApiException(EXISTS_SEQUENCE_EXCEPTION);
    }

    additionalMenu.setMenuName(additionalMenuDto.getMenuName());
    additionalMenu.setSequence(additionalMenuDto.getSequence());
    additionalMenu.setPrice(additionalMenuDto.getPrice());
    return additionalMenuRepository.save(additionalMenu);
  }

  public AdditionalMenu deleteAdditionalMenu(Authentication authentication, String storeId, long additionalId) {
    Store store = getStoreWithMatchUser(authentication, storeId);
    AdditionalMenu additionalMenu = additionalMenuRepository.findById(additionalId).orElseThrow(()-> new RestApiException(ADDITIONAL_MENU_NOT_FOUND));
    isMatchStore(store, additionalMenu.getMenu().getMenuId().toString());

    additionalMenu.setDeletedAt(LocalDateTime.now());

    return additionalMenuRepository.save(additionalMenu);
  }

  private Menu isMatchStore(Store store, String menuId) {
    Menu menu = menuRepository.findById(Long.parseLong(menuId)).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));
    if(!Objects.equals(store.getStoreId(), menu.getMenuCategory().getStore().getStoreId())) {
      throw new RestApiException(MISMATCH_STORE_MENU);
    }
    return menu;
  }


  public AdditionalMenuDto updateSoldOutAdditionalMenu(Authentication authentication, String additionalId, String storeId, boolean soldOut) {
    Store store = getStoreWithMatchUser(authentication, storeId);
    AdditionalMenu additionalMenu = additionalMenuRepository.findById(Long.parseLong(additionalId)).orElseThrow(() -> new RestApiException(ADDITIONAL_MENU_NOT_FOUND));
    isMatchStore(store, additionalMenu.getMenu().getMenuId().toString());
    additionalMenu.setSoldOutStatus(soldOut);

    return new AdditionalMenuDto(additionalMenuRepository.save(additionalMenu));
  }

  public MenuDto updateSoldOutStoreMenu(Authentication authentication, String storeId,
      String menuId, boolean soldOut) {
    getStoreWithMatchUser(authentication, storeId);
    Menu menu = menuRepository.findById(Long.valueOf(menuId)).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));
    menu.setSoldOutStatus(soldOut);
    return new MenuDto(menuRepository.save(menu));
  }

  public MenuDto selectMenuDetails(Authentication authentication, String storeId, String menuId, RoleType type) {
    if(type == RoleType.ROLE_PARTNER)
      getStoreWithMatchUser(authentication, storeId);
    Menu menu = menuRepository.findById(Long.valueOf(menuId)).orElseThrow(() -> new RestApiException(MENU_NOT_FOUND));
    MenuDto menuDto = new MenuDto(menu);
    menuDto.setAdditionalMenuList(additionalMenuRepository.findAllByMenu(menu).stream().map(AdditionalMenuDto::new).toList());

    return menuDto;
  }
}
