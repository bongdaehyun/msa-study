package com.sparta.msa_exam.cart;

import com.sparta.msa_exam.cart.dto.Cart;
import com.sparta.msa_exam.cart.dto.CartDto;
import com.sparta.msa_exam.cart.dto.CartItem;
import com.sparta.msa_exam.cart.dto.CartItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    private final String keyString = "cart:%s";
    private final RedisTemplate<String, CartItemDto> cartTemplate;
    private final HashOperations<String, String, CartItemDto> hashOps;
    private final CartRepository cartRepository;

    public CartService(RedisTemplate<String, CartItemDto> cartTemplate,CartRepository cartRepository) {
        this.cartTemplate = cartTemplate;
        this.hashOps = this.cartTemplate.opsForHash();
        this.cartRepository = cartRepository;
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

    @Transactional
    public void transferCartToDB(String sessionId, String username) {
        //  로그인 전 장바구니의 물품이 있는 경우에만 실행
        if (!hashOps.entries(keyString.formatted(sessionId)).isEmpty())
        {
            log.info("size of Redis" + hashOps.entries(keyString.formatted(sessionId)).size());

            Map<String,CartItemDto> items = hashOps.entries(keyString.formatted(sessionId));

            Cart cart = Cart.builder()
                    .username(username)
                    .build();

            items.values().forEach(
                    cartItemDto -> {
                        CartItem cartItem = CartItem.fromCartItemDto(cartItemDto);
                        cartItem.addCart(cart);
                        cart.addCartItem(cartItem);
                    }
            );

            cartRepository.save(cart);
            //레디스에 있는 값제거
            //log.info("saved to db" + keyString.formatted(sessionId));
            //hashOps.delete(keyString.formatted(sessionId));
        }

    }
}
