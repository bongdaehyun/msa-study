package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductClient productClient;

    @InjectMocks
    private OrderService orderService;


    @Test
    @DisplayName("주문 추가 성공 ")
    void test1(){
        OrderRequestDto requestDto = new OrderRequestDto("test",new ArrayList<>());
        requestDto.getOrderItems().add(new OrderItemDto(1L));
        Order order = Order.createOrder(requestDto);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        given(productClient.getProductById(1L)).willReturn(productResponseDto);

        Order newOrder = orderService.createOrder(requestDto);

        assertNotNull(newOrder);
        assertEquals(order.getOrderId(), newOrder.getOrderId());
        assertEquals(order.getProduct_ids().size(), newOrder.getProduct_ids().size());

    }

    @Test
    @DisplayName("주문 추가 실패 ")
    void test2(){
        OrderRequestDto requestDto = new OrderRequestDto("test",new ArrayList<>());
        requestDto.getOrderItems().add(new OrderItemDto(1L));

        ProductResponseDto productResponseDto = new ProductResponseDto();
        given(productClient.getProductById(1L)).willReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {orderService.createOrder(requestDto); }
        );

        assertEquals(exception.getMessage(),"존재하지 않는 상품ID 입니다.");
    }

    @Test
    @DisplayName("주문 조회 성공 ")
    void test3(){
        Order order = new Order(1L,"test",new ArrayList<>());

        given(orderRepository.findOrderAndItemById(1L)).willReturn(order);

        OrderResponseDto dto = orderService.getOrderDetail(1L);

        assertNotNull(dto);
        assertEquals(dto.getOrder_id(),order.getOrderId());

    }

    @Test
    @DisplayName("주문 조회 실패 ")
    void test4(){
        Order order = new Order(1L,"test",new ArrayList<>());

        given(orderRepository.findOrderAndItemById(1L)).willReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {orderService.getOrderDetail(1L); }
        );

        assertEquals(exception.getMessage(),"잘못된 상품번호 입니다.");
    }

}