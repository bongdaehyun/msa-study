package com.sparta.msa_exam.order.orders;

import lombok.Data;

import java.util.List;


@Data
public class OrderRequestDto {
    private String name;
    private List<OrderItemDto> orderItems;
}
