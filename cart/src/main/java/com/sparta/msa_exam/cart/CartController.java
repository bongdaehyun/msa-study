package com.sparta.msa_exam.cart;

import com.sparta.msa_exam.cart.dto.CartDto;
import com.sparta.msa_exam.cart.dto.CartItemDto;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /***
     *
     * @param itemDto
     * @param session
     * @return
     */
    @PostMapping
    public CartDto modifyCart( @RequestBody CartItemDto itemDto, HttpSession session) {
        cartService.modifyCart(session.getId(), itemDto);
        return cartService.getAllCarts(session.getId());
    }

    /***
     * 장바구니 전체 조회
     ***/
    @GetMapping
    public CartDto getAllCarts(HttpSession session){
        return cartService.getAllCarts(session.getId());
    }

    /***
     * 로그인한 사용자라면 장바구니 데이터베이스로 이동
     */
    @PostMapping("/transfer")
    public void transferCartToDB(HttpSession session,
                                 @RequestHeader("X-USER-NAME") String username)
    {
        log.info("header value :" + username);
        cartService.transferCartToDB(session.getId(),username);
    }
}
