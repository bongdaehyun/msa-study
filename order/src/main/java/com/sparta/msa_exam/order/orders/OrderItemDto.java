package com.sparta.msa_exam.order.orders;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDto implements Serializable {
    private Long product_id;
}
