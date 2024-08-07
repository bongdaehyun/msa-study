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
5. 분산 시스템에서의 데이터 일관성을 어떻게 유지하나요?

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