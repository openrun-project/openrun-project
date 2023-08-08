package com.project.openrun.product.service;

import com.project.openrun.global.exception.ProductException;
import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.web.server.ResponseStatusException;


import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;


//    @Test
//    @DisplayName("상품 전체 조회 가능한가")
//    void test() {
//        // given
//        Product product1 = Product.builder()
//                .id(1L)
//                .productName("test")
//                .price(1000)
//                .wishCount(0)
//                .mallName("test mall")
//                .totalQuantity(30)
//                .currentQuantity(30)
//                .category("category")
//                .productImage("test Image")
//                .eventStartTime(LocalDateTime.now())
//                .build();
//
//        Product product2 = Product.builder()
//                .id(2L)
//                .productName("test2")
//                .price(1000)
//                .wishCount(0)
//                .mallName("test mall2")
//                .totalQuantity(30)
//                .currentQuantity(30)
//                .category("category2")
//                .productImage("test Image2")
//                .eventStartTime(LocalDateTime.now())
//                .build();
//
//        //when
//        when(productRepository.findAll()).thenReturn(Arrays.asList(product1, product2));
//        List<AllProductResponseDto> result = productService.getAllProducts();
//
//
//        //then
//        assertThat(result).extracting("productName").containsExactly("test", "test2");
//        assertThat(result.size()).isEqualTo(2);
//    }
//
//    @Test
//    @DisplayName("상품이 없을 때 빈 리스트가 나오는가")
//    void test1() {
//        //when
//        when(productRepository.findAll()).thenReturn(Collections.emptyList());
//        List<AllProductResponseDto> result = productService.getAllProducts();
//
//        //then
//        assertThat(result.size()).isEqualTo(0);
//    }

    @Test
    @DisplayName("특정 상품 조회가 가능한가")
    void test3() {
        // given
        Product product1 = Product.builder()
                .id(1L)
                .productName("test")
                .price(1000)
                .wishCount(0)
                .mallName("test mall")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category")
                .productImage("test Image")
                .eventStartTime(LocalDateTime.now())
                .build();

        //when
        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        DetailProductResponseDto result = productService.getDetailProduct(1L);


        //then
        assertThat(result.mallName()).isEqualTo("test mall");
    }

    @Test
    @DisplayName("없는 상품을 조회 시, 에러 발생 여부")
    void test4() {

        //when
        when(productRepository.findById(2L)).thenThrow(ResponseStatusException.class);

        //then
        assertThatThrownBy(
                        () -> productService.getDetailProduct(2L)
                ).isInstanceOf(ResponseStatusException.class);
    }
}