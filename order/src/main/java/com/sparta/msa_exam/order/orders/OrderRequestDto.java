package com.sparta.msa_exam.order.orders;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class OrderRequestDto {
    private String name;
    private List<OrderItemDto> orderItems;
}
