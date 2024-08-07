package com.sparta.msa_exam.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductRequestDto {
    private String name;

    private Integer supply_price;
}
