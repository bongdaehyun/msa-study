package com.sparta.msa_exam.order.orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue
    private Long orderId;

    private String name;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> product_ids = new ArrayList<>();

    public static Order createOrder(OrderRequestDto requestDto)
    {
        Order order = Order.builder()
                .name(requestDto.getName())
                .product_ids(new ArrayList<>())
                .build();
        //OrderItem 생성 및 연관관게 설정
        for (OrderItemDto orderItemDto : requestDto.getOrderItems()) {
            OrderItem orderItem = OrderItem.builder()
                    .product_id(orderItemDto.getProduct_id())
                    .order(order)
                    .build();

            order.getProduct_ids().add(orderItem);
        }

        return order;
    }

    public static OrderResponseDto toOrderResponseDto(Order order) {
        List<OrderItemDto> lists = order.getProduct_ids().stream()
                .map(OrderItem::toOrderItemDto).toList();
        return new OrderResponseDto(order.getOrderId(),lists);
    }
}
