package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    /***
     * 주문 추가하는 API
     * @param requestDto
     * @return Order
     */
    @Transactional
    public OrderResponseDto createOrder(OrderRequestDto requestDto) {

        //상품 목록 존재 검증
        for(OrderItemDto orderItemDto : requestDto.getOrderItems()) {
            ProductResponseDto productResponseDto = productClient.getProductById(orderItemDto.getProduct_id());
            if (productResponseDto == null) {
                throw new IllegalArgumentException("존재하지 않는 상품ID 입니다.");
            }
        }

        Order newOrder = Order.createOrder(requestDto);
        orderRepository.save(newOrder);

        return Order.toOrderResponseDto(newOrder);
    }


    /***
     * 주문의 상세한 정보 조회 API
     * @param orderId
     * @return OrderResponseDto
     * 캐시기능 추가 : 60초동안 redis에서 관리
     */
    @Cacheable(value = "orderCache", key = "args[0]")
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderDetail(long orderId) {
        log.info("getOrderDetail 실행~~~");
        //해당 order 존재 검증
        Order order = orderRepository.findOrderAndItemById(orderId);

        if(order == null) {
            throw new IllegalArgumentException("잘못된 상품번호 입니다.");
        }

        return Order.toOrderResponseDto(order);
    }
}
