package com.sparta.msa_exam.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/products/{id}")
    ProductResponseDto getProductById(@PathVariable("id") Long id);

    @GetMapping("/products")
    List<ProductResponseDto> getAllProducts();
}
