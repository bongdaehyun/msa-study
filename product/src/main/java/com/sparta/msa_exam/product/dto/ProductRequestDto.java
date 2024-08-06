package com.sparta.msa_exam.product.dto;

import lombok.Data;

@Data
public class ProductRequestDto {
    private String name;

    private Integer supply_price;
}
