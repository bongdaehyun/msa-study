package com.sparta.msa_exam.order.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderResponseDto {
    private Long order_id;
    private List<OrderItemDto> product_ids;

}
