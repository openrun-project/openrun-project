package com.project.openrun.product.service;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.entity.OpenRunStatus;
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
import org.springframework.web.server.ResponseStatusException;

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


    @Test
    @DisplayName("상품 전체 조회 가능한가")
    void test() {
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

        Product product2 = Product.builder()
                .id(2L)
                .productName("test2")
                .price(1000)
                .wishCount(0)
                .mallName("test mall2")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category2")
                .productImage("test Image2")
                .eventStartTime(LocalDateTime.now())
                .build();

        //when
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(productRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Arrays.asList(product1, product2), pageRequest, 2));
        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);


        //then
        assertThat(result).extracting("productName").containsExactly("test", "test2");
        assertThat(result.getContent().size()).isEqualTo(2);

    }

    @Test
    @DisplayName("상품이 없을 때 빈 리스트가 나오는가")
    void test1() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        //when
        when(productRepository.findAll(pageRequest)).thenReturn(new PageImpl<>(Collections.emptyList(), pageRequest, 0));
        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);

        //then
        assertThat(result).isNull();
    }

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

    @Test
    @DisplayName("인기 상품 조회 테스트")
    void test5() {
        // given
        Product product1 = Product.builder()
                .id(1L)
                .productName("test1")
                .price(1000)
                .wishCount(1)
                .mallName("test mall")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category")
                .productImage("test Image")
                .eventStartTime(LocalDateTime.now())
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("test2")
                .price(1000)
                .wishCount(2)
                .mallName("test mall2")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category2")
                .productImage("test Image2")
                .eventStartTime(LocalDateTime.now())
                .build();

        Product product3 = Product.builder()
                .id(3L)
                .productName("test3")
                .price(1000)
                .wishCount(3)
                .mallName("test mall2")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category2")
                .productImage("test Image2")
                .eventStartTime(LocalDateTime.now())
                .build();

        when(productRepository.findTopCountProduct(3L)).thenReturn(Arrays.asList(product3, product2, product1));
        List<AllProductResponseDto> result = productService.getTopCountProducts(3L);

        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("productName").containsExactly("test3", "test2", "test1");
    }

    @Test
    @DisplayName("오픈런 상품 조회 테스트")
    public void getOpenrunAllProductsTest(){
        // given
        Product product1 = Product.builder()
                .id(1L)
                .productName("test1")
                .price(1000)
                .wishCount(1)
                .mallName("test mall")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category")
                .productImage("test Image")
                .eventStartTime(LocalDateTime.now())
                .status(OpenRunStatus.OPEN)
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productName("test2")
                .price(1000)
                .wishCount(2)
                .mallName("test mall2")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category2")
                .productImage("test Image2")
                .eventStartTime(LocalDateTime.now())
                .status(OpenRunStatus.OPEN)
                .build();

        //when
        PageRequest pageRequest = PageRequest.of(0, 10);
        when(productRepository.findAllByStatusOrderByWishCountDesc(OpenRunStatus.OPEN, pageRequest)).thenReturn(new PageImpl<>(Arrays.asList(product2, product1), pageRequest, 2));
        Page<AllProductResponseDto> openrunAllProducts = productService.getOpenrunAllProducts(pageRequest);

        //then
        assertThat(openrunAllProducts).extracting("productName").containsExactly("test2", "test1");
    }

}