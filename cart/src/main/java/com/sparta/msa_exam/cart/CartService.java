package com.sparta.msa_exam.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartService {

    private final RedisTemplate<String, String> cartTemplate;
    private final HashOperations<String, String, String> cartHashOperations;

}
