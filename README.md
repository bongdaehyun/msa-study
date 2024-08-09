# 생각해봐야될 질문들
1. 레거시 모놀리식 서비스를 MSA 로 전환한다고 할 때, 어떠한 이유가 있을 수 있을까요 ?
     - 모놀리식 서비스는 하나의 서비스에서 에러가 발생하면 전체 서비스가 동작을 멈추게 됩니다. 그래서 독립적인 서비스의 형태를 가져 안정성을 높힐 수 있습니다.
     - 확장성이 뛰어납니다. 다른 서비스를 추가하고 테스트를 할 때에 다른 서비스에 대한 의존성이 없기 때문에 유지보수도 좋은 거 같습니다. 
2. 고객 주문 시 예상치 못한 오류로 인해 상품 서비스에서 재고를 차감 하였으나, 주문 서비스에서 에러가 발생해 주문이 정상적으로 완료되지 않았을 경우 어떻게 할 수 있을까요 ?
     - 재고는 차감했으나 주문 서비스가 에러가 났을 경우에는 다시 재고를 복구를 해야될 거 같습니다.
     - 오류가 발생된 건을 일정 시간마다 호출하면서 재시도를 하면 좋을 거 같습니다.
3. 마이크로서비스 간의 통신에 대한 보안을 어떻게 보장할 수 있을까요?
     - api gateway를 이용하여 클라이언트 요청을 받아 인증을 수행하고, 유효한 요청만을 마이크로서비스에 보내주면 될 거 같습니다.
     - jwt토큰을 이용하여 검증된 사용자만이 접근가능하도록 하면 될 거 같습니다.
4. MSA 환경에서 서로의 서비스를 의존하게 되는 의존성 순환이 발생했을때 어떻게 해결할 수 있을까요 ?
   - 코레오그래피 사가를 적용해서 의존성 순환을 해결합니다. (인터넷 검색..)
   - 이벤트 기반으로 각 서비스가 자율적으로 동작
   - Ex) 주문 시 "주문 생성" 이벤트 발행 -> 주문 생성 이벤트 수신 및 재고 확인
     -> 재고확인 결과 이벤트 발행 -> 재고 관련 이벤트에 따라 주문 상태 업데이트
5. 분산 시스템에서의 데이터 일관성을 어떻게 유지하나요?
   - 각 서비스마다 디비를 따로 관리하는 상황에서는 공통적으로 사용하는 데이터들을 모아두는 디비를 이용해서 크로스체크를 하면서 데이터의 일관성을 체크해야될 거 같다.
   - 예를 들면 gateway에서 여러 서비스의 데이터를 조합해서 관리(?) 
   - 그리고 데이터에 따른 버전을 관리하면서 충돌을 대비해야될 거 같다.

## 추가한 내용
1. 주문에 상품을 추가할때 상품목록 조회 API를 이용하지 않고 해당 id에 대한 값들을 가져와서 검증
```java
//in OrderService
        //상품 목록 존재 검증
        for(OrderItemDto orderItemDto : requestDto.getOrderItems()) {
            ProductResponseDto productResponseDto = productClient.getProductById(orderItemDto.getProduct_id());
            if (productResponseDto == null) {
                throw new IllegalArgumentException("존재하지 않는 상품ID 입니다.");
            }
        }
```

2. 주문 단건 조회시 querydsl이용
- findById를 이용하면 Order와 연관되어있는 OrderItem까지 2번의 쿼리가 실행
- 쿼리 한번으로 조회하기 위하여 querydsl의 fetchjoin사용
```java
public Order findOrderAndItemById(Long orderId) {

        return jpaQueryFactory.selectFrom(QOrder.order)
                .leftJoin(QOrder.order.product_ids,QOrderItem.orderItem).fetchJoin()
                .where(
                        QOrder.order.orderId.eq(orderId)
                )
                .fetchOne();

    }
```

# 구현한 기능들
## Product
1. 상품 추가 API
2. 상품 목록 조회 API
3. 상품 ID로 조회 API

## Order
1. 주문 추가 API
2. 주문에 상품 추가하는 API
3. 주문 단건 조회 API

## Auth
1. 로그인(db연결 X)
2. 회원가입 (db와 연결) 

## Gateway
1. JWT 검증 로직,인증 구현
2. Response header 에 server port 추가 

# 과제하면서 어려웠던 점
1. 주문이나 상품에 성공적으로 추가한 케이스는 어떻게 테스트코드를 짜야되는지
   - 검증하는 로직에 걸리지 않으면 제대로 추가되었다고 판단해도 되는지.
2. gateway에서 server port를 추가하는 방법과 각 도메인에서 Filter를 통해 서버포트를 추가하는 방법 중 어느 것이 올바른 것인지.?
   - 후자의 방법으로는 중복되는 코드를 여러번 사용해야되고 추후 변경될 때 유지보수가 힘듬
   - 전자의 방법은 로드밸런스가 서비스까지 제대로 뻗지 못하면 실행이 되지 못한다.
     - 튜터분들의 의견이 달라서 궁금
3. 캐싱되어있는 메소드는 따로 테스트코드 단에서 테스트를 어떻게 하는가?