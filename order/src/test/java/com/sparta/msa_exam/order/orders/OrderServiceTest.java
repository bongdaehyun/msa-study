package com.sparta.msa_exam.order.orders;

import com.sparta.msa_exam.order.client.ProductClient;
import com.sparta.msa_exam.order.client.ProductResponseDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
    void testCreateOrderSuccess(){
        OrderRequestDto requestDto = new OrderRequestDto("test",new ArrayList<>());
        requestDto.getOrderItems().add(new OrderItemDto(1L));
        Order order = Order.createOrder(requestDto);
        given(orderRepository.save(any(Order.class))).willReturn(order);

        ProductResponseDto productResponseDto = new ProductResponseDto();
        given(productClient.getProductById(1L)).willReturn(productResponseDto);

        OrderResponseDto newOrder = orderService.createOrder(requestDto);

        assertNotNull(newOrder);
        assertEquals(order.getOrderId(), newOrder.getOrder_id());
        assertEquals(order.getProduct_ids().size(), newOrder.getProduct_ids().size());

    }

    @Test
    @DisplayName("존재하지 않는 상품을 주문시 추가 실패 ")
    void testCreateOrderUnsuccessful(){
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
    void testFindOrderSuccess(){
        Order order = new Order(1L,"test",new ArrayList<>());

        given(orderRepository.findOrderAndItemById(1L)).willReturn(order);

        OrderResponseDto dto = orderService.getOrderDetail(1L);

        assertNotNull(dto);
        assertEquals(dto.getOrder_id(),order.getOrderId());

    }

    @Test
    @DisplayName("존재하지 않는 주문을 조회시 실패 ")
    void testFindOrderUnsuccessful(){
        Order order = new Order(1L,"test",new ArrayList<>());

        given(orderRepository.findOrderAndItemById(1L)).willReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> {orderService.getOrderDetail(1L); }
        );

        assertEquals(exception.getMessage(),"잘못된 상품번호 입니다.");
    }

    @Test
    @DisplayName("존재하지 않는 주문번호로 주문에 상품을 추가하는 경우")
    void testUpdateOrderUnsuccessfulByNotExistingOrder(){
        Order order = new Order(1L,"test",new ArrayList<>());
        given(orderRepository.findOrderAndItemById(1L)).willReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, ()
        -> orderService.updateOrderItem(order.getOrderId(),new AddItemRequestDto())

        );

        assertEquals(exception.getMessage(),"잘못된 주문 ID입니다.");
    }

    @Test
    @DisplayName(" 주문에 존재하지 앓는 상품을 추가하는 경우")
    void testUpdateOrderUnSuccessfullByNotExistingOrderItem(){
        Order order = new Order(1L,"test",new ArrayList<>());

        given(orderRepository.findOrderAndItemById(1L)).willReturn(order);

        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        productResponseDtoList.add(new ProductResponseDto(1L,"test",1));

        given(productClient.getAllProducts()).willReturn(productResponseDtoList);

        Exception exception = assertThrows(IllegalArgumentException.class, ()
                -> orderService.updateOrderItem(order.getOrderId(),new AddItemRequestDto(3L))

        );

        assertEquals(exception.getMessage(),"존재하지 않는 상품입니다.");
    }

    @Test
    @DisplayName("주문에 상품을 성공적으로 추가하는 경우")
    void testUpdateOrderSuccessfullByExistingOrderItem(){
        Order order = new Order(1L,"test",new ArrayList<>());

        given(orderRepository.findOrderAndItemById(1L)).willReturn(order);

        List<ProductResponseDto> productResponseDtoList = new ArrayList<>();
        productResponseDtoList.add(new ProductResponseDto(1L,"test",1));
        productResponseDtoList.add(new ProductResponseDto(2L,"test",2));

        given(productClient.getAllProducts()).willReturn(productResponseDtoList);

        orderService.updateOrderItem(order.getOrderId(),new AddItemRequestDto(1L));

    }
}