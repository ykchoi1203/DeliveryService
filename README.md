# 주문 배달 서비스 API
음식을 주문하고 리뷰를 남길 수 있는 서비스

# 개발 환경
- Framework: Spring Boot
- Persistence: JPA
- Language: Java
- Build Tool: Gradle
- Database: Mysql
- elasticsearch
- IDEA : IntelliJ
- api : 카카오페이 api, 카카오맵 api, 카카오 로그인 api


# 프로젝트 기능 및 설계
  
## 일반 회원 /users
- [x] 회원가입
  - POST /signup
  - 사용자는 회원가입 할 수 있다. 일빈 회원은 회원가입 시 USER 권한을 가진다.
  - 회원가입 시 아이디, 비밀번호, 닉네임, 핸드폰 번호, 이메일을 입력받으며, 비밀번호를 제외한 값들은 unique 해야한다.
  - 소셜 회원가입 시 유저 아이디와 비밀번호는 자동 생성되며, 소셜 tid, 닉네임, 핸드폰 번호, 이메일을 입력받으며, 해당 값들은 unique 해야한다.
    
- [x] 일반 로그인
  - POST /signin
  - 사용자는 로그인 할 수 있다. 로그인 시 아이디와 패스워드가 일치해야 한다.
  - 로그인 성공 시 jwt 토큰을 발급받는다.
     
- [x] 소셜 로그인
  - POST /signin/oauth2/kakao
  - 사용자는 소셜 로그인 할 수 있다. 카카오 로그인에 성공 후, 해당 고유 소셜 아이디가 존재한다면 로그인 성공. 로그인 성공 시 Jwt 토큰을 발급받는다
  - 해당 소셜 아이디가 존재하지 않는다면 회원가입 페이지로 넘어간다.
  - 이미 존재하는 이메일이지만 provideId 값이 없는 경우 로그인 페이지로 이동 -> 로그인 성공시 provideId 값을 업데이트 해준다

- [x] 프로필 수정
  - PUT /
  - 사용자는 프로필을 수정 할 수 있다. 비밀번호, 이름, 닉네임, 핸드폰 번호, 이메일을 입력 받는다.

- [x] 회원 탈퇴
  - PUT /delete
  - 사용자는 탈퇴를 할 수 있다. 탈퇴에 성공하면 삭제 날짜를 업데이트 한다.
     
- [x] 배달 주소 list 조회
  - GET /address
  - 사용자는 배달 주소 리스트를 조회 할 수 있다.

- [x] 배달 주소 등록
  - POST /address
  - 사용자는 최대 5개까지의 배달 주소를 등록 할 수 있다. 배달 주소(address1: 도/특별시, address2: 구/시/군/면, address3: 나머지 주소), 배달지 이름을 입력받는다.
  - 해당 등록 주소가 기본 설정된다. 

- [x] 배달 주소 수정
  - PUT /address/{addressId}
  - 사용자는 배달 주소를 수정 할 수 있다. 배달 주소(address1: 도/특별시, address2: 구/시/군/면, address3: 나머지 주소), 배달지 이름, 기본 주소로 설정하는지의 여부를 입력받는다.
  - 해당 아이디의 등록된 주소 중 기본 주소 설정이 true인 값이 존재하는 경우 기존 주소 설정을 false로 바꾸고 주소를 업데이트 시킨다.

- [x] 배달 주소 삭제
  - PUT /addresses/{addressId}/delete
  - 사용자는 배달 주소를 삭제 할 수 있다.
  - 해당 주소가 기본 주소로 설정되어 있었다면, 가장 최근에 등록한 주소가 기본 주소 값으로 설정된다.

- [x] 가게 검색
  - GET /store
  - 사용자는 기본 주소로 설정된 근처 5km의 가게를 검색한다.
  - category, orderType, ASC/DESC, 검색 query의 설정으로 조회도 가능하다.
  - query 입력 시 query가 해당 가게명 또는 메뉴명에 포함되어 있는 가게를 리스트에 포함시킨다.

- [x] 가게 상세 조회
  - GET /{storeId}/menu
  - 사용자는 해당되는 가게의 메뉴를 조회 할 수 있다.

- [x] 메뉴 상세 조회
  - GET {storeId}/menu/menu/{menuId}
  - 사용자는 해당되는 가게의 메뉴를 상세 조회 할 수 있다.
  - 추가 메뉴가 있으면 추가 메뉴도 같이 조회된다.

- [x] 장바구니 조회
  - GET /cart
  - 사용자는 메뉴를 장바구니를 조회할 수 있다. 장바구니에는 같은 가게의 메뉴만 담겨 있다.
  - redis에 userEmail이 키인 값을 조회한다.

- [x] 장바구니 추가
  - POST /cart
  - 사용자는 메뉴를 장바구니에 추가할 수 있다. 메뉴, 추가메뉴 리스트, 수량을 입력받는다.
  - 장바구니에는 같은 가게의 메뉴만 추가할 수 있다.
  - redis에 user-email를 키로 메뉴가 담겨있다. 담겨있다면 해당 메뉴의 가게Id 값과 비교하여 추가 여부를 결정한다.
  - 해당 메뉴들의 total-cost도 같이 redis에 저장된다.

- [x] 장바구니 수정
  - POST /cart/{cartId}
  - 사용자는 메뉴를 장바구니에 수정할 수 있다. 추가메뉴 리스트, 수량을 입력받는다.
  - 해당 메뉴들의 total-cost도 같이 redis에 저장된다.

- [x] 장바구니 삭제
  - DELETE /cart/{cartId}
  - 사용자는 메뉴를 장바구니에서 삭제할 수 있다.
  - 해당 장바구니에서 id 값으로 삭제한다.

- [x] 주문
  - POST /order
  - 사용자는 주문할 수 있다. 요청 사항을 입력받는다. 장바구니에 담겨있는 메뉴들을 주문한다.
  - 가게의 최소 주문 금액보다 총 주문 금액이 작다면 주문 할 수 없다.
  - 결제 페이지(/users/payment/kakao/ready)로 redirect 한다.
    
- [x] 결제 요청
  - POST /payment/kakao/ready
  - 카카오 페이로 결제를 요청한다.
  - 카카오 페이 요청 양식으로 바꾸어 카카오 페이 결제 요청 페이지로 post 요청을 보낸다.
  - 성공 시 users/payment/kakao/success로 redirect 보낸다.

- [x] 결제 완료 승인
  - POST /payment/kakao/success
  - 카카오 페이로 결제 완료 승인을 받기 위해 카카오 페이 승인 페이지로 post 요청을 보낸다. 
  - 완료 승인이 되면 users/order/success 로 post 요청을 보낸다.

- [x] 주문 성공
  - POST /order/success
  - 주문 상태가 wait에서 PAYMENT로 저장된다. 장바구니에 있는 메뉴들을 주문 내역에 추가해준다.
  - 주문이 완료된 메뉴들은 장바구니에서 삭제된다.
  - 결제 history 테이블에 결제한 내역이 추가된다.

- [x] 주문 목록 조회
  - GET /order
  - 사용자는 주문한 내역을 확인 할 수 있다.
  - 페이징 처리를 통해 한번에 20개씩 조회 할 수 있다.
  
- [x] 주문 내역 상세보기
  - GET /order/{orderId}
  - 사용자는 선택한 주문 내역의 정보를 확인 할 수 있다.

- [x] 주문 내역 상세보기
  - POST /order/{orderId}/delivery
  - 사용자가 배달을 직접 받은 후 아직 주문 상태가 ACCEPT 상태라면 DELIVERY_COMPLETED 상태로 변경할 수 있다.
  - deliveryTime 이 업데이트된다. 

- [x] 리뷰 리스트 확인
  - GET /review
  - 사용자는 작성한 리뷰 리스트를 확인 할 수 있다. 최근에 작성한 리뷰 순으로 정렬된다.
  - 페이징 처리를 통해 한번에 20개씩 조회 할 수 있다.

- [x] 리뷰 작성하기
  - POST /review/{orderId}
  - 사용자는 배달 완료된 상태의 주문에 대해 리뷰를 남길 수 있다. 해당 주문일로부터 3일 내에만 작성 가능하다.
  - 별점 (1 ~ 5), 코멘트, 리뷰 사진을 입력할 수 있고, 코멘트와 리뷰 사진은 입력하지 않아도 된다. 리뷰 사진은 한장만 등록 가능하다.
  - 리뷰 작성이 완료되면 해당 별점이 가게 별점에 적용된다.
  - elasticsearch의 가게 star 도 업데이트 한다.

- [x] 리뷰 수정하기
  - PUT /review/{reviewId}
  - 사용자는 작성했던 리뷰를 수정할 수 있다.
  - 사진 변경시 기존 사진은 삭제날짜가 추가된다.
  - elasticsearch의 가게 star 도 업데이트 한다.

- [x] 리뷰 삭제하기
  - PUT /review/{reviewId}/delete
  - 사용자는 작성했던 리뷰를 삭제할 수 있다. 해당 리뷰의 삭제 날짜가 업데이트 된다.
  - 삭제가 완료되면 해당 가게의 totalStars 와 totalReview 가 수정된다.
  - elasticsearch의 가게 star 도 업데이트 한다.

- [x] 찜 목록 확인
  - GET /wish
  - 사용자는 찜한 목록을 조회할 수 있다. 추가한 최신 순으로 정렬된다.
  - 페이징 처리를 통해 최대 10개씩 조회 가능하다.

- [x] 가게 찜하기
  - POST /wish/{storeId}
  - 사용자는 해당 가게를 찜할 수 있다.

- [x] 가게 찜 삭제
  - DELETE /wish/{wishId}
  - 사용자는 해당 가게의 찜을 삭제할 수 있다.

## 사장님 /partners
- [x] 회원가입
  - POST /signup
  - 사용자는 회원가입 할 수 있다. 사장님 회원은 회원가입 시 partner 권한을 가진다.
  - 회원가입 시 아이디, 비밀번호, 핸드폰 번호, 주소, 이메일을 입력받으며, 비밀번호를 제외한 값들은 unique 해야한다.
    
- [x] 로그인
  - POST /signin
  - 사용자는 로그인 할 수 있다. 로그인 시 아이디와 패스워드가 일치해야 한다.

- [x] 프로필 수정
  - PUT /
  - 사용자는 프로필을 수정 할 수 있다. 비밀번호, 이름, 닉네임, 핸드폰 번호, 주소, 이메일을 입력 받는다.

- [x] 회원 탈퇴
  - PUT /delete
  - 사용자는 탈퇴를 할 수 있다. 탈퇴에 성공하면  삭제 날짜를 업데이트 한다.

- [x] 가게 등록
  - POST /store
  - 사용자는 가게를 등록할 수 있다.
  - 상호명, 전화번호, 주소, 사업자번호, 카테고리, 오픈 시간, 마감 시간, 가게 설명, 최소 주문 금액, 배달비 금액, 원산지 표기를 입력받는다.
  - 해당 주소의 위도 경도 값은 카카오맵 api를 통해 얻는다.
  - 등록에 성공하면 가게 access 상태는 false로 된다.

- [x] 가게 사진 추가
 - POST /store/photo/{storeId}
 - 가게 사진을 추가할 수 있다.

- [x] 가게 사진 변경
 - PUT /store/photo/{storeId}
 - 가게 사진을 변경할 수 있다.
 - 기존 사진의 deletedAt 날짜를 업데이트한다.

- [x] 가게 프로필 사진 추가
 - POST /store/photo/{storeId}/profile
 - 가게 프로필 사진을 추가할 수 있다.

- [x] 가게 프로필 사진 변경
 - PUT /store/photo/{storeId}/profile
 - 가게 프로필 사진을 변경할 수 있다.
 - 기존 프로필 사진의 deletedAt 날짜를 업데이트한다.

- [x] 가게 수정
  - PUT /store/{storeId}
  - 사용자는 가게를 수정할 수 있다.
  - 상호명, 전화번호, 주소, 사업자번호, 카테고리, 오픈 시간, 마감 시간, 가게 설명, 최소 주문 금액, 배달비 금액, 원산지 표기 입력 받을 수 있다.
  - 해당 주소의 위도 경도 값은 카카오맵 api를 통해 얻는다.
  - 해당 elasticsearch의 데이터도 수정한다

- [x] 가게 삭제
  - PUT /store/{storeId}/delete
  - 사용자는 가게를 삭제 수 있다.
  -  해당 가게의 삭제 날짜가 업데이트 된다.

- [x] 가게 리스트 조회
  - GET /store
  - 사용자는 자신의 가게들을 조회할 수 있다.
  - 페이징 처리 된다.

- [x] 가게 메뉴 카테고리 조회
  - GET /{storeId}/category
  - 사용자는 자신의 가게의 메뉴 카테고리를 조회할 수 있다.
  - 카테고리의 순서 값 순서로 조회된다.

- [x] 가게 메뉴 카테고리 추가
  - POST /{storeId}/category
  - 사용자는 자신의 가게의 메뉴 카테고리를 추가할 수 있다.
  - 카테고리 이름, 순서를 입력받는다.

- [x] 가게 메뉴 카테고리 수정
  - POST /{storeId}/category/{categoryId}
  - 사용자는 자신의 가게의 메뉴 카테고리를 수정 수 있다.
  -  카테고리 이름, 순서를 입력받는다.
  -  해당 순서가 존재하는 경우 등록에 실패한다.

- [x] 가게 메뉴 카테고리 삭제
  - PUT /{storeId}/category/{categoryId}/delete
  - 사용자는 자신의 가게의 메뉴 카테고리를 삭제할 수 있다.
  - 해당 메뉴 카테고리에 등록된 메뉴가 있다면 삭제할 수 없.

- [x] 가게 메뉴 조회
  - GET /{storeId}/menu
  - 사용자는 자신이 선택한 가게의 메뉴를 조회할 수 있다.
  - 메뉴와 메뉴 사진을 보여준다.

- [x] 가게 메뉴 상세 조회
  - GET /{storeId}/menu/{menuId}
  - 사용자는 자신의 가게의 메뉴를 상세 조회할 수 있다. 해당 메뉴와 해당 메뉴의 추가메뉴, 사진을 조회할 수 있다.

- [x] 가게 메뉴 등록
  - POST /{storeId}/menu
  - 사용자는 메뉴를 추가할 수 있다.
  - 메뉴 이름, 가격, 설명, 메뉴 카테고리를 입력받는다.
  - 메뉴 카테고리가 존재가지 않는다면 등록할 수 없다.
  - 해당 elasticsearch의 데이터(해당 store의 메뉴 추가, menuDocumet 추가)도 추가한다.

- [x] 가게 메뉴 수정
  - PUT /{storeId}/menu/{menuId}
  - 사용자는 메뉴를 수정할 수 있다. 메뉴 이름, 가격, 설명, 메뉴 카테고리를 입력받는다.\
  - 해당 elasticsearch의 데이터도 수정한다

- [x] 가게 메뉴 삭제
  - PUT /{storeId}/menu/{menuId}/delete
  - 사용자는 메뉴를 삭제할 수 있다.
  - 삭제 날짜가 업데이트된다.
  - 해당 elasticsearch의 데이터도 삭제된다.

- [x] 메뉴 품절 처리
  - PUT /{storeId}/menu/{menuId}/soldOut
  - 사용자는 가게가 오픈 상태일 때 해당 메뉴를 품절 처리 할 수 있다.

- [x] 메뉴 재판매 처리
  - PUT /{storeId}/menu/{menuId}/resale
  - 사용자는 가게가 오픈 상태일 때 해당 메뉴를 판매 가 상태로 변경할 수 있다.

- [x] 가게 메뉴 사진 등록
  - POST /{storeId}/menu/{menuId}/photo
  - 사용자는 메뉴 사진을 등록할 수 있다.

- [x] 가게 메뉴 사진 변경
  - PUT /{storeId}/menu/{menuId}/photo
  - 사용자는 메뉴 사진을 변경할 수 있다.
  - 기존 사진은 삭제 날짜가 업데이트된다.

- [x] 추가 메뉴 등록
  - POST /{storeId}/addition/{menuId}
  - 사용자는 메뉴에 대한 추가 메뉴를 등록할 수 있다. 추가 메뉴 이름, 가격, 순서를 입력받는다.
  - 해당 순서가 존재하는 경우 등록에 실패한다.

- [x] 추가 메뉴 수정
  - PUT /{storeId}/addition/{additionalId}
  - 사용자는 메뉴에 대한 추가 메뉴를 수정할 수 있다. 추가 메뉴 이름, 가격, 순서를 입력받는다.
  - 해당 순서가 존재하는 경우 등록에 실패한다.

- [x] 추가 메뉴 삭제
  - PUT /{storeId}/addition/{additionalId}/delete
  - 사용자는 메뉴에 대한 추가 메뉴를 삭제할 수 있다.
  - 삭제 날짜가 업데이트 된다.

- [x] 추가 메뉴 품절 처리
  - PUT /{storeId}/addition/{additionalId}/soldOut
  - 사용자는 메뉴에 대한 추가 메뉴를 품절 처리 할 수 있다.

- [x] 추가 메뉴 재판매 처리
  - PUT /{storeId}/addition/{additionalId}/resale
  - 사용자는 메뉴에 대한 추가 메뉴를 재판매 처리 할 수 있다.

- [x] 주문 조회
  - GET /order/{storeId}
  - 사용자는 현재 날짜의 주문을 조회할 수 있다.
  - 최근 오픈 시간 이후의 주문으로 조회 하며, 최신 순으로 정렬된다.
  - 페이징 처리를 통해 최대 20개씩 조회 가능하다.

- [x] 주문 상세 조회
  - GET /order/{storeId}/{orderId}
  - 사용자는 해당 주문을 상세 조회할 수 있다.

- [x] 주문 수락
  - PUT /order/{storeId}/{orderId}/accept
  - 사용자는 주문 상태가 PAMENT인 주문에 대해 주문 수락 할 수 있다.

- [x] 주문 거절
  - PUT /order/{storeId}/{orderId}/refuse
  - 사용자는 주문 상태가 PAMENT인 주문에 대해 주문 거절 할 수 있다.

- [x] 배달 완료 처리
  - PUT /order/{storeId}/{orderId}/complete
  - 사용자는 주문 상태가 ACCEPT인 주문의 배달 완료 처리할 수 있다.

- [x] 영업 상태 변경(오픈)
  - PUT /store/{storeId}/open
  - 사용자는 영업 상태를 open으로 변경할 수 있다.
  - 최근 영업 오픈 시간이 업데이트 된다.
  - 품절 처리되었던 메뉴와 추가 메뉴들이 판매 가능 상태가 된다.

- [x] 영업 상태 변경(종료)
  - PUT /store/{storeId}/close
  - 사용자는 영업 상태를 close로 변경할 수 있다.
  - 최근 오픈 날짜 이후의 PAYMENT 나 ACCEPT 상태인 주문이 있다면 종료할 수 없다.
  - 종료 성공 시 최근 영업 종료 시간이 업데이트 된다.

      
## 관리자 /admin 

- [x] POST /signin
  - 관리자는 로그인 할 수 있다. 

- [x] 가게 카테고리 리스트 조회
  - GET /category
  - 관리자는 가게 카테고리 리스트를 조회할 수 있다.

- [x] 가게 카테고리 추가
  - PUT /category
  - 관리자는 가게 카테고리를 추가할 수 있다. 카테고리 명과 순서를 입력받는다.
  - 해당 순서가 존재한다면 등록에 실패한다.

- [x] 가게 카테고리 수정
  - PUT /category
  - 관리자는 가게 카테고리를 수정할 수 있다. 카테고리 명과 순서를 입력받는다.
  - 해당 순서가 존재한다면 수정에 실패한다.

- [x] 가게 카테고리 수정
  - PUT /category
  - 관리자는 가게 카테고리를 수정할 수 있다. 카테고리 명과 순서를 입력받는다.
  - 해당 순서가 존재한다면 수정에 실패한다.

- [x] 가게 카테고리 삭제
  - DELETE /category
  - 관리자는 가게 카테고리를 삭제할 수 있다.
  - 해당 카테고리에 가게가 존재한다면 삭제할 수 없다.

- [x] 가게 리스트 조회
  - GET /store
  - 관리자는 가게 카테고리 리스트를 조회할 수 있다.
  - 최근 등록한 순서대로 나온다.

- [ ] 가게 상세 조회
  - GET /store/{storeId}
  - 관리자는 가게 상세 조회할 수 있다.

- [x] 가게 허가
  - PUT /store/{storeId}/access
  - 관리자는 가게 허가해 줄 수 있다.
  - 허가가 되면 해당 가게를 유저는 검색할 수 있다.

- [x] 가게 허가 취소
  - PUT /store/{storeId}/cancel
  - 관리자는 가게 허가 취소할 수 있다.
  - 허가가 되면 해당 가게를 유저는 조회할 수 없게 된다.





# ERD
![delivery](https://github.com/ykchoi1203/DeliveryService/assets/30820741/8e24dbd7-ad5f-4600-9148-5f6ca7e1cb45)


## Trouble Shooting
[go to the trouble shooting section](TROUBLE_SHOOTING.md)
