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
import com.younggeun.delivery.store.domain.dto.PhotoDto;
import com.younggeun.delivery.store.domain.dto.StoreDto;
import com.younggeun.delivery.store.domain.entity.Store;
import com.younggeun.delivery.store.domain.type.OrderType;
import com.younggeun.delivery.user.domain.DeliveryAddressRepository;
import com.younggeun.delivery.user.domain.UserRepository;
import com.younggeun.delivery.user.domain.entity.DeliveryAddress;
import com.younggeun.delivery.user.domain.entity.User;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.GeoDistanceSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
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

  private final ElasticsearchRestTemplate elasticsearchRestTemplate;


  public List<StoreDto> searchStores(Authentication authentication, OrderType type, String query, String categoryId,
      boolean asc, int page) {
    User user = getUser(authentication);
    DeliveryAddress deliveryAddress = getDeliveryAddress(user);
    Set<Long> storeIds;
    Pageable pageable;
    // 쿼리와 카테고리가 조건으로 안들어온 경우 20개 검색
    if(query.isEmpty() && Long.parseLong(categoryId) == 0) {
      pageable = PageRequest.of(page, 20);
      storeIds = storeDocumentRepository.findByAddress1AndAddress2(deliveryAddress.getAddress1(),
          deliveryAddress.getAddress2(), pageable).stream().map(StoreDocument::getId).collect(Collectors.toSet());

      return getStoreDtos(type, deliveryAddress, storeIds.stream().toList(), asc);
    }
    pageable = PageRequest.of(page, 10);
    // 가게명으로 먼저 검색
    if(Long.parseLong(categoryId) == 0) {
      storeIds = storeDocumentRepository.findByAddress1AndAddress2AndNameContaining(
              deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), query, pageable)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet());
    } else {
      storeIds = storeDocumentRepository.findByCategoryIdAndAddress1AndAddress2AndNameContaining(Long.parseLong(categoryId),
              deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), query, pageable)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet());
    }

    // 메뉴명으로 검색
    List<Long> storeIdByMenu = menuDocumentRepository.findByNameContaining(query, pageable).stream().map(MenuDocument::getStoreId).distinct().toList();

    if(Long.parseLong(categoryId) == 0) {
      storeIds.addAll(storeDocumentRepository.findByAddress1AndAddress2AndIdIn(deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), storeIdByMenu, pageable)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet()));
    } else {
      storeIds.addAll(storeDocumentRepository.findByCategoryIdAndAddress1AndAddress2AndIdIn(Long.parseLong(categoryId), deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), storeIdByMenu, pageable)
          .stream().map(StoreDocument::getId).collect(Collectors.toSet()));
    }

    // 쿼리가 포함된 가게명의 ID set 에 쿼리에 포함된 메뉴가 있는 가게 list id 를 합친 후 list 를 Repository 에서 가져온다.
    storeIds.addAll(storeDocumentRepository.findByAddress1AndAddress2AndIdIn(deliveryAddress.getAddress1(), deliveryAddress.getAddress2(), storeIdByMenu, pageable).stream().map(StoreDocument::getId).collect(
        Collectors.toSet()));

    return getStoreDtos(type, deliveryAddress, storeIds.stream().toList(), asc);
  }

  public List<StoreDocument> findStores(Authentication authentication, OrderType type, String distance, Long categoryId, String query, String asc) {
    User user = getUser(authentication);
    DeliveryAddress deliveryAddress = getDeliveryAddress(user);
    Sort.Direction direction = asc.equals("ASC") ? Direction.ASC : Direction.DESC;
    System.out.println(direction.toString());

    NativeSearchQuery searchQuery = buildSearchQuery(deliveryAddress, distance, categoryId, query, type, direction);
    // SearchSourceBuilder 사용하여 쿼리 내용을 JSON 형식으로 출력
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(searchQuery.getQuery());
    searchQuery.getElasticsearchSorts().forEach(searchSourceBuilder::sort);

    // 쿼리 내용을 출력
    String queryContent = searchSourceBuilder.toString();
    System.out.println(queryContent);

    SearchHits<StoreDocument> searchHits = elasticsearchRestTemplate.search(searchQuery, StoreDocument.class);
    return searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
  }

  private NativeSearchQuery buildSearchQuery(DeliveryAddress deliveryAddress, String distance, Long categoryId, String query, OrderType type,
      Direction direction) {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.geoDistanceQuery("location")
            .point(deliveryAddress.getLatitude(), deliveryAddress.getLongitude())
            .distance(distance));

    if (categoryId != 0) {
      queryBuilder.must(QueryBuilders.termQuery("categoryId", categoryId));
    }

    if (!query.isEmpty()) {
      BoolQueryBuilder nameOrMenuQuery = QueryBuilders.boolQuery()
          .should(QueryBuilders.matchQuery("name", query))
          .should(QueryBuilders.nestedQuery("menuDocumentList",
              QueryBuilders.matchQuery("menuDocumentList.name", query), ScoreMode.Max));

      queryBuilder.must(nameOrMenuQuery);
    }

    SortOrder sortOrder = direction == Direction.ASC ? SortOrder.ASC : SortOrder.DESC;
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(queryBuilder);

    switch (type) {
      case COST:
        searchSourceBuilder.sort("deliveryCost", sortOrder);
        break;
      case DIST:
        GeoPoint geoPoint = new GeoPoint(deliveryAddress.getLatitude(), deliveryAddress.getLongitude());
        GeoDistanceSortBuilder geoDistanceSortBuilder = new GeoDistanceSortBuilder("location", geoPoint)
            .unit(DistanceUnit.METERS)
            .order(sortOrder);
        searchSourceBuilder.sort(geoDistanceSortBuilder);
        break;
      case STAR:
        searchSourceBuilder.sort("stars", sortOrder);
        break;
      default:
        searchSourceBuilder.sort("_score", sortOrder);
        break;
    }

    // 쿼리 내용 출력
    System.out.println(queryBuilder.toString());

    return new NativeSearchQueryBuilder()
        .withQuery(queryBuilder)
        .withMinScore(1.0f)
        .withSorts(searchSourceBuilder.sorts())
        .build();
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
    List<Long> storeIds = list.stream().map(Store::getStoreId).toList();

    Map<Long, PhotoDto> storePhotoList = storePhotoRepository.findAllByStoreStoreIdIn(storeIds).stream().map(PhotoDto::new).collect(
        Collectors.toMap(PhotoDto::getStoreId, photoDto -> photoDto));
    Map<Long, PhotoDto> storeProfilePhotoList = storeProfilePhotoRepository.findAllByStoreStoreIdIn(storeIds).stream().map(PhotoDto::new).collect(Collectors.toMap(PhotoDto::getStoreId, photoDto -> photoDto));
    List<StoreDto> storeDtoList = list.stream().map(StoreDto::new).toList();

    storeDtoList.forEach(item -> {
      Long storeId = item.getStoreId();
      PhotoDto storePhoto = storePhotoList.get(storeId);
      PhotoDto storeProfilePhoto = storeProfilePhotoList.get(storeId);
      item.setStorePhoto(storePhoto);
      item.setStoreProfilePhoto(storeProfilePhoto);
    });

    return storeDtoList;
  }
}
