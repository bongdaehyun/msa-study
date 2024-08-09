package com.sparta.msa_exam.product.dto;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ProductResponseDto implements Serializable {
    private Long product_id;

    private String name;

    private Integer supply_price;
}
