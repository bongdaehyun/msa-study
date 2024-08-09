package com.sparta.msa_exam.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto implements Serializable {
    private String name;

    private Integer supply_price;
}
