package com.sparta.msa_exam.cart;

import com.sparta.msa_exam.cart.dto.CartDto;
import com.sparta.msa_exam.cart.dto.CartItemDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class CartService {

    private final String keyString = "cart:%s";
    private final RedisTemplate<String, CartItemDto> cartTemplate;
    private final HashOperations<String, String, CartItemDto> hashOps;

    public CartService(RedisTemplate<String, CartItemDto> cartTemplate) {
        this.cartTemplate = cartTemplate;
        this.hashOps = this.cartTemplate.opsForHash();
    }

    public CartDto getAllCarts(String sessionId) {
        boolean exists = Optional.ofNullable(cartTemplate.hasKey(keyString.formatted(sessionId)))
                .orElse(false);
        if (!exists)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);

        //만료시간 셋팅 30초(절대적인 시간으로 TTL을 설정)
        Date expireAt = Date.from(Instant.now().plus(30, ChronoUnit.SECONDS));
        cartTemplate.expireAt(
                keyString.formatted(sessionId),
                expireAt
        );
        /*
        상대적인 시간으로 TTL을 설정 (예: 30분 후).
        cartTemplate.expire(keyString.formatted(sessionId),30, TimeUnit.MINUTES);
         */

        return CartDto.toDtoOfHashSet(
                hashOps.entries(keyString.formatted(sessionId)),
                expireAt
        );
    }

    public void modifyCart(String sessionId, CartItemDto itemDto) {
        CartItemDto existedDto = hashOps.get(keyString.formatted(sessionId), itemDto.getItemName());

        if(existedDto != null)
        {
            int updateQuantity = existedDto.getItemQuantity() + itemDto.getItemQuantity();

            if(updateQuantity < 0)
            {
                hashOps.delete(keyString.formatted(sessionId), itemDto.getItemName());
            }
        }else if(itemDto.getItemQuantity() > 0){
            hashOps.put(keyString.formatted(sessionId),itemDto.getItemName(),itemDto);
        }
    }
}
