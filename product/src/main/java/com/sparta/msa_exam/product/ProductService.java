package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.dto.Product;
import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductResponseDto> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream().map(Product::toProductResponseDto).toList();
    }

    @Transactional
    public void createProduct(ProductRequestDto requestDto) {
        Product createProduct = Product.builder()
                .name(requestDto.getName())
                .supply_price(requestDto.getSupply_price())
                .build();

        Product saveProduct = productRepository.save(createProduct);

        if(saveProduct == null){
            throw new RuntimeException("Product not created");
        }
    }

    public ProductResponseDto getProductById(Long productId) {
        Optional<Product> product = productRepository.findById(productId);

        if(product.isPresent()){
            return Product.toProductResponseDto(product.get());
        }

        return null;
    }
}
