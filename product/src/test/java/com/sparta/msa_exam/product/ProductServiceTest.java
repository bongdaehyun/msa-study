package com.sparta.msa_exam.product;

import com.sparta.msa_exam.product.dto.Product;
import com.sparta.msa_exam.product.dto.ProductRequestDto;
import com.sparta.msa_exam.product.dto.ProductResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }


    @Test
    @DisplayName("상품 목록 조회 테스트")
    void testGetAllProducts(){
        //given
        Product product1 = new Product(1L, "test1",1000);
        Product product2 = new Product(2L, "test2",2000);

        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);

        given(productRepository.findAll()).willReturn(productList);

        //when
        List<ProductResponseDto> lists = productService.getAllProducts();

        //then
        assertNotNull(lists);
        assertEquals(lists.size(), 2);
        assertEquals(lists.get(0).getName(), product1.getName());
    }

    @Test
    @DisplayName("상품 생성 실패")
    void testCreateProductUnsuccessful(){
        ProductRequestDto responseDto = new ProductRequestDto("test",1000);

        given(productRepository.save(any(Product.class))).willReturn(null);

        //when
        Exception thrown = assertThrows(RuntimeException.class, () -> {
            productService.createProduct(responseDto);
        });

        assertEquals(thrown.getMessage(), "Product not created");

    }

    @Test
    @DisplayName("상품 생성 성공")
    void testCreateProductSuccessful(){
        ProductRequestDto responseDto = new ProductRequestDto("test",1000);
        Product product = Product.builder()
                .name(responseDto.getName())
                .supply_price(responseDto.getSupply_price())
                .build();

        given(productRepository.save(any(Product.class))).willReturn(product);

        //when
        assertDoesNotThrow( () -> productService.createProduct(responseDto));
    }

    @Test
    @DisplayName("상품 id로 조회")
    void testGetProductById(){
        Product product = Product.builder()
                .product_id(1L)
                .name("test")
                .supply_price(1234)
                .build();

        given(productRepository.findById(product.getProduct_id())).willReturn(Optional.of(product));

        ProductResponseDto dto = productService.getProductById(product.getProduct_id());

        assertEquals(dto.getName(), product.getName());
        assertEquals(dto.getSupply_price(), product.getSupply_price());


    }
}