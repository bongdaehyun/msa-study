package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    /***
     * 주문 내용과 주문한 상품들을 같이 추가하는 API
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
    @Cacheable(cacheNames = "orderCache", key = "args[0]")
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

    /***
     * 존재하는 주문내역에 상품을 추가하는 API
     * @param orderId
     * @param requestDto : product_id
     */
    @CachePut(cacheNames = "orderCache", key = "#orderId")
    //@CacheEvict(allEntries = true)
    @Transactional
    public void updateOrderItem(long orderId, AddItemRequestDto requestDto) {
        //주문 찾기
        Order order = orderRepository.findOrderAndItemById(orderId);
        if(order == null) {
            throw new IllegalArgumentException("잘못된 주문 ID입니다.");
        }
        //상품목록 조회 API호출하여 상품 존재검증
        List<ProductResponseDto> product_lists = productClient.getAllProducts();

        boolean existedProduct = product_lists.stream().anyMatch(
                productResponseDto -> productResponseDto.getProduct_id() == requestDto.getProduct_id()
        );

        if(existedProduct) {
            order.updateItem(requestDto.getProduct_id());
        }
        else
            throw new IllegalArgumentException("존재하지 않는 상품입니다.");

    }
}
