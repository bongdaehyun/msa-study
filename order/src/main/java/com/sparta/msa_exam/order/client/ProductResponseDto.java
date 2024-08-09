package com.sparta.msa_exam.order.client;

import lombok.Data;

@Data
public class ProductResponseDto {
    private Long product_id;
    private String name;
    private Integer supply_price;
}
