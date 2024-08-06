package com.sparta.msa_exam.product.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProductResponseDto {
    private Long product_id;

    private String name;

    private Integer supply_price;
}
