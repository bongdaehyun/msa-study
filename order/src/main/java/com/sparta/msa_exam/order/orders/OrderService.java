package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    @Transactional
    public Order createOrder(OrderRequestDto requestDto) {

        //상품 목록 존재 검증
        for(OrderItemDto orderItemDto : requestDto.getOrderItems()) {
            ProductResponseDto productResponseDto = productClient.getProductById(orderItemDto.getProduct_id());
            if (productResponseDto == null) {
                throw new IllegalArgumentException("존재하지 않는 상품ID 입니다.");
            }
        }

        Order newOrder = Order.createOrder(requestDto);
        orderRepository.save(newOrder);
        return newOrder;
    }

    @Transactional(readOnly = true)
    public OrderResponseDto getOrderDetail(long orderId) {
        //해당 order 존재 검증
        Order order = orderRepository.findOrderAndItemById(orderId);
        if(order == null) {
            throw new IllegalArgumentException("잘못된 상품번호 입니다.");
        }

        return Order.toOrderResponseDto(order);
    }
}
