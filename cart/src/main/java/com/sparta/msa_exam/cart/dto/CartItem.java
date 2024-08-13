package com.sparta.msa_exam.cart.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    private String itemName;
    private Integer itemPrice;
    private Integer itemQuantity;

    public static CartItem fromCartItemDto(CartItemDto cartItemDto){
        return CartItem.builder()
                .itemName(cartItemDto.getItemName())
                .itemPrice(cartItemDto.getItemPrice())
                .itemQuantity(cartItemDto.getItemQuantity())
                .build();
    }

    public void addCart(Cart cart){
        this.cart = cart;
    }
}
