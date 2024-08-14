package com.sparta.msa_exam.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CartDto {
    private Set<CartItemDto> items;
    private Date expiredAt;

    public static CartDto toDtoOfHashSet(
            Map<String,CartItemDto> cartItemDtoMap,
            Date expiredAt
    ){
        return CartDto.builder()
                .items(cartItemDtoMap.values().stream()
                        .map(entity -> CartItemDto.builder()
                                .itemName(entity.getItemName())
                                .itemQuantity(entity.getItemQuantity())
                                .itemPrice(entity.getItemPrice())
                                .build()).collect(Collectors.toUnmodifiableSet())
                )

                .expiredAt(expiredAt)
                .build();
    }
}
