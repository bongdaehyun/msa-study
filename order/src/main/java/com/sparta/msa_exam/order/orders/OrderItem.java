package com.sparta.msa_exam.order.orders;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //외래키를 가진 객체가 주인으로 설정
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    private Long product_id;

    public static OrderItemDto toOrderItemDto(OrderItem orderItem){
        OrderItemDto orderItemDto = new OrderItemDto(orderItem.getProduct_id());
        return orderItemDto;
    }
}
