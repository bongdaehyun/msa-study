package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /*
    상품목록 조회
     */
    @GetMapping
    public ResponseEntity<?> getAllProducts() {

        return ResponseEntity.ok(productService.getAllProducts());
    }

    /*
    상품 추가
     */
    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody ProductRequestDto requestDto){
        productService.createProduct(requestDto);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
