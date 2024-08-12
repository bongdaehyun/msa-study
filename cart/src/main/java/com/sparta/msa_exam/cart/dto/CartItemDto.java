package com.sparta.msa_exam.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CartItemDto implements Serializable {
    private String itemName;
    private Integer itemPrice;
    private Integer itemQuantity;
}
