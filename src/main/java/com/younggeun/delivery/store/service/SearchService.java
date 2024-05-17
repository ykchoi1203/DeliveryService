package com.younggeun.delivery.store.service;

import static com.younggeun.delivery.global.exception.type.UserErrorCode.ADDRESS_NOT_FOUND;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.MISMATCH_USER_ADDRESS_EXCEPTION;
import static com.younggeun.delivery.global.exception.type.UserErrorCode.USER_NOT_FOUND_EXCEPTION;

import com.younggeun.delivery.global.exception.RestApiException;
import com.younggeun.delivery.store.domain.StorePhotoRepository;
import com.younggeun.delivery.store.domain.StoreProfilePhotoRepository;
import com.younggeun.delivery.store.domain.StoreRepository;
import com.younggeun.delivery.store.domain.documents.MenuDocument;
import com.younggeun.delivery.store.domain.documents.StoreDocument;
import com.younggeun.delivery.store.domain.documents.repository.MenuDocumentRepository;
import com.younggeun.delivery.store.domain.documents.repository.StoreDocumentRepository;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.entity.StorePhoto;
import com.younggeun.delivery.store.domain.entity.StoreProfilePhoto;
import com.younggeun.delivery.store.domain.type.OrderType;
import com.younggeun.delivery.user.domain.DeliveryAddressRepository;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.User;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class SearchService {
  private final StoreRepository storeRepository;

  private final StoreDocumentRepository storeDocumentRepository;
  private final MenuDocumentRepository menuDocumentRepository;

  private final StorePhotoRepository storePhotoRepository;
  private final StoreProfilePhotoRepository storeProfilePhotoRepository;

  private final UserRepository userRepository;
  private final DeliveryAddressRepository addressRepository;

  public List<StoreDto> searchStores(Authentication authentication, OrderType type, String query, String categoryId,
      boolean asc) {
    User user = getUser(authentication);
    DeliveryAddress deliveryAddress = getDeliveryAddress(user);
    Set<Long> storeIds;
    // 쿼리와 카테고리가 조건으로 안들어온 경우
    if(query.isEmpty() && Long.parseLong(categoryId) == 0) {
      storeIds = storeDocumentRepository.findByAddress1AndAddress2(deliveryAddress.getAddress1(),
          deliveryAddress.getAddress2()).stream().map(StoreDocument::getId).collect(Collectors.toSet());

      return getStoreDtos(type, deliveryAddress, storeIds.stream().toList(), asc);
    }

    // 가게명으로 먼저 검색
    if(Long.parseLong(categoryId) == 0) {
      storeIds = storeDocumentRepository.findByAddress1AndAddress2AndNameContaining(
              deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), query)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet());
    } else {
      storeIds = storeDocumentRepository.findByCategoryIdAndAddress1AndAddress2AndNameContaining(Long.parseLong(categoryId),
              deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), query)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet());
    }

    // 메뉴명으로 검색
    List<Long> storeIdByMenu = menuDocumentRepository.findByNameContaining(query).stream().map(MenuDocument::getStoreId).distinct().toList();

    if(Long.parseLong(categoryId) == 0) {
      storeIds.addAll(storeDocumentRepository.findByAddress1AndAddress2AndIdIn(deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), storeIdByMenu)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet()));
    } else {
      storeIds.addAll(storeDocumentRepository.findByCategoryIdAndAddress1AndAddress2AndIdIn(Long.parseLong(categoryId), deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), storeIdByMenu)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet()));
    }

    // 쿼리가 포함된 가게명의 ID set 에 쿼리에 포함된 메뉴가 있는 가게 list id 를 합친 후 list 를 Repository 에서 가져온다.
    storeIds.addAll(storeDocumentRepository.findByAddress1AndAddress2AndIdIn(deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), storeIdByMenu).stream().map(StoreDocument::getId).collect(
        Collectors.toSet()));

    return getStoreDtos(type, deliveryAddress, storeIds.stream().toList(), asc);
  }

  private List<StoreDto> getStoreDtos(OrderType type, DeliveryAddress deliveryAddress,
      List<Long> storeDocumentIdList, boolean asc) {
    if(type == OrderType.STAR) {
      return asc ? storeToDto(storeRepository.findAllByStoreIdInOrderByStar(storeDocumentIdList)) :
          storeToDto(storeRepository.findAllByStoreIdInOrderByStarDesc(storeDocumentIdList));
    } else if(type == OrderType.DIST) {
      return asc ? storeToDto(storeRepository.findAllByStoreIdInByDist(storeDocumentIdList, deliveryAddress.getLatitude(), deliveryAddress.getLongitude()))
       : storeToDto(storeRepository.findAllByStoreIdInByDistDesc(storeDocumentIdList, deliveryAddress.getLatitude(), deliveryAddress.getLongitude()));
    } else if(type == OrderType.COST) {
      return asc ? storeToDto(storeRepository.findAllByStoreIdInOrderByDeliveryCostAsc(storeDocumentIdList)) :
          storeToDto(storeRepository.findAllByStoreIdInOrderByDeliveryCostDesc(storeDocumentIdList));
    } else {
      return asc ? storeToDto(storeRepository.findAllByStoreIdInOrderByStar(storeDocumentIdList)) :
          storeToDto(storeRepository.findAllByStoreIdInOrderByStarDesc(storeDocumentIdList));
    }
  }
  private User getUser(Authentication authentication) {
    return userRepository.findByEmail(authentication.getName())
        .orElseThrow(() -> new RestApiException(USER_NOT_FOUND_EXCEPTION));
  }

  private DeliveryAddress getDeliveryAddress(User user) {
    DeliveryAddress deliveryAddress = addressRepository.findByUserAndDefaultAddressStatusIsTrue(user).orElseThrow(() -> new RestApiException(ADDRESS_NOT_FOUND));

    if(!Objects.equals(user.getUserId(), deliveryAddress.getUser().getUserId())) {
      throw new RestApiException(MISMATCH_USER_ADDRESS_EXCEPTION);
    }

    return deliveryAddress;
  }

  private List<StoreDto> storeToDto(List<Store> list) {
    return list.stream().map(store -> {
      StoreDto storeDto = new StoreDto(store);

      StorePhoto storePhoto = storePhotoRepository.findByStore(store)
          .orElse(null);

      storeDto.setStorePhoto(storePhoto);

      StoreProfilePhoto storeProfilePhoto = storeProfilePhotoRepository.findByStore(store).orElse(null);

      storeDto.setStoreProfilePhoto(storeProfilePhoto);

      return storeDto;
    }).toList();
  }
}
