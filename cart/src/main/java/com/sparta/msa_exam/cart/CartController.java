package com.sparta.msa_exam.cart;

import com.sparta.msa_exam.cart.dto.CartRequestDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    /***
     * 장바구니 전체 조회
     ***/
    @GetMapping
    public CartRequestDto getAllCarts(HttpSession session){
        CartService.getAllCarts(session);
    }

}
