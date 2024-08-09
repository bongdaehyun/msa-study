package com.sparta.msa_exam.order.orders;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OrderResponseDto implements Serializable {
    private Long order_id;
    private List<OrderItemDto> product_ids;

}
