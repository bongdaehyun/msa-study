package com.sparta.msa_exam.order.orders;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

        try {
            OrderResponseDto responseDto = orderService.createOrder(requestDto);
            return ResponseEntity.ok(responseDto);
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /***
     *  주문에 상품 추가
     */
    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable("orderId") Long orderId,
                                         @RequestBody AddItemRequestDto requestDto) {
        try {
            orderService.updateOrderItem(orderId,requestDto);
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /***
     *  주문 단건 조회
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable("orderId") long orderId) {
        try {
            OrderResponseDto responseDto = orderService.getOrderDetail(orderId);
            return ResponseEntity.ok(responseDto);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }
}
