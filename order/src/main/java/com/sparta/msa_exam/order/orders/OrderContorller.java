package com.sparta.msa_exam.order.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderContorller {

    private final OrderService orderService;

    /***
    주문 추가 API
    ***/
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody OrderRequestDto requestDto) {
        Order order = new Order();
        try {
            order = orderService.createOrder(requestDto);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

        return ResponseEntity.ok(order);
    }

    /***
     *  주문에 상품 추가
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable("orderId") long orderId,
                                         @RequestBody OrderRequestDto requestDto) {
        return null;

    }
    /***
     *  주문 단건 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") long orderId) {

        return ResponseEntity.ok(orderService.getOrderDetail(orderId));
    }
}
