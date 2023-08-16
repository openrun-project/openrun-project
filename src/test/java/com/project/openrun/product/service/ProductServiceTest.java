package com.project.openrun.product.service;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.AllProductResponseDtos;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheRedisRepository<AllProductResponseDto> allProductRedisRepository;

    @Mock
    private CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;

    @InjectMocks
    private ProductService productService;


//    @Test
//    @DisplayName("redis에 저장된 데이터가 없을 때, 상품 전체 조회 가능한가")
//    void testRedis_no() {
////        // given
////        AllProductResponseDto product1 = new AllProductResponseDto(
////                1L
////                ,"test1"
////                ,0
////                ,"test mall1"
////                ,"category1"
////
////        );
////
////        AllProductResponseDto product2 = new AllProductResponseDto(
////                2L
////                ,"test2"
////                ,0
////                ,"test mall2"
////                ,"category2"
////        );
//        PageRequest pageRequest = PageRequest.of(0, 10);
//
//        AllProductResponseDto product = mock(AllProductResponseDto.class);
//        Page<AllProductResponseDto> dbPage = new PageImpl<>(Arrays.asList(product), pageRequest, 1);
//
//        //when
//        when(allProductRedisRepository.getProduct(0)).thenReturn(null);
//        when(productRepository.findAllDto(pageRequest)).thenReturn(dbPage);
//
//        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);
//
//
//        //then
////        assertThat(result).extracting("productName").containsExactly("test1", "test2");
////        assertThat(result.getContent().size()).isEqualTo(2);
//        assertThat(result).isEqualTo(dbPage);
//        verify(allProductRedisRepository, times(1)).saveProduct(0, dbPage);
//
//    }

//    @Test
//    @DisplayName("redis에 저장된 데이터가 있을 때, 상품 전체 조회 가능한가")
//    void testRedis_yes() {
////        // given
////        AllProductResponseDto product1 = new AllProductResponseDto(
////                1L
////                ,"test1"
////                ,0
////                ,"test mall1"
////                ,"category1"
////
////        );
////
////        AllProductResponseDto product2 = new AllProductResponseDto(
////                2L
////                ,"test2"
////                ,0
////                ,"test mall2"
////                ,"category2"
////        );
//        PageRequest pageRequest = PageRequest.of(0, 10);
//
//        AllProductResponseDto product = mock(AllProductResponseDto.class);
//        Page<AllProductResponseDto> dbPage = new PageImpl<>(Arrays.asList(product), pageRequest, 1);
//
//        //when
//        when(allProductRedisRepository.getProduct(0)).thenReturn(dbPage);
//
//        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);
//
//
//        //then
////        assertThat(result).extracting("productName").containsExactly("test1", "test2");
////        assertThat(result.getContent().size()).isEqualTo(2);
//        assertThat(result).isEqualTo(dbPage);
//
//    }

//    @Test
//    @DisplayName("상품이 없을 때 빈 리스트가 나오는가")
//    void test1() {
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        //when
//        when(productRepository.findAllDto(pageRequest)).thenReturn(new PageImpl<>(Collections.emptyList(), pageRequest, 0));
//        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);
//
//        //then
//        assertThat(result).isNull();
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
        List<AllProductResponseDtos> result = productService.getTopCountProducts(3L);

        assertThat(result.size()).isEqualTo(3);
        assertThat(result).extracting("productName").containsExactly("test3", "test2", "test1");
    }

//    @Test
//    @DisplayName("오픈런 상품 조회 테스트")
//    public void getOpenrunAllProductsTestRedis_no(){
////        // given
////        OpenRunProductResponseDto product1 = new OpenRunProductResponseDto(
////                1L,
////                "test1",
////                "testImg",
////                1000,
////                "의류",
////                "의류"
////        );
////
////        OpenRunProductResponseDto product2 = new OpenRunProductResponseDto(
////                2L,
////                "test2",
////                "testImg2",
////                2000,
////                "의류",
////                "의류"
////        );
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        OpenRunProductResponseDto product = mock(OpenRunProductResponseDto.class);
//        Page<OpenRunProductResponseDto> pageResult = new PageImpl<>(Arrays.asList(product), pageRequest, 1);
//
//        //when
//        when(openRunProductRedisRepository.getProduct(anyInt())).thenReturn(null);
//        when(productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageRequest)).thenReturn(pageResult);
//
//        Page<OpenRunProductResponseDto> openrunAllProducts = productService.getOpenRunAllProducts(pageRequest);
//
//        //then
////        assertThat(openrunAllProducts).extracting("productName").containsExactly("test2", "test1");
//        assertThat(openrunAllProducts).isEqualTo(pageResult);
//        verify(openRunProductRedisRepository, times(1))
//                .saveProduct(pageRequest.getPageNumber(), pageResult);
//    }
//    @Test
//    @DisplayName("오픈런 상품 조회 테스트")
//    public void getOpenrunAllProductsTestRedis_yes(){
//        // given
//        OpenRunProductResponseDto product1 = new OpenRunProductResponseDto(
//                1L,
//                "test1",
//                "testImg",
//                1000,
//                "의류",
//                "의류"
//        );
//
//        OpenRunProductResponseDto product2 = new OpenRunProductResponseDto(
//                2L,
//                "test2",
//                "testImg2",
//                2000,
//                "의류",
//                "의류"
//        );
//
//        //when
//        PageRequest pageRequest = PageRequest.of(0, 10);
//        when(productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageRequest)).thenReturn(new PageImpl<>(Arrays.asList(product2, product1), pageRequest, 2));
//        Page<OpenRunProductResponseDto> openrunAllProducts = productService.getOpenRunAllProducts(pageRequest);
//
//        //then
//        assertThat(openrunAllProducts).extracting("productName").containsExactly("test2", "test1");
//    }

}