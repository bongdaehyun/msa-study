package com.sparta.msa_exam.order.orders;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto implements Serializable {
    private Long order_id;
    private List<OrderItemDto> product_ids;

}
