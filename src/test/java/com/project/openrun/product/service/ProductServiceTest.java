package com.project.openrun.product.service;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.DetailProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import com.project.openrun.product.repository.ProductsSearchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CacheRedisRepository<AllProductResponseDto> allProductRedisRepository;

    @Mock
    private CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;

    @Mock
    private ProductsSearchRepository productsSearchRepository;

    @InjectMocks
    private ProductService productService;


    @Disabled
    @Test
    @DisplayName("redis에 저장된 데이터가 없을 때, 상품 전체 조회 가능한가")
    void getAllProducts_Test_No_Redis() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 1);
        Long productCount = 100L;
        AllProductResponseDto product = mock(AllProductResponseDto.class);
        Page<AllProductResponseDto> dbPage = new PageImpl<>(Arrays.asList(product), pageRequest, 1);

        when(allProductRedisRepository.getProduct(0)).thenReturn(null);
        when(allProductRedisRepository.getProductCount()).thenReturn(Optional.of(productCount));
        when(productRepository.findAllDto(pageRequest, productCount)).thenReturn(dbPage);

        //when
        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);

        //then
        assertThat(result).isEqualTo(dbPage);
        verify(allProductRedisRepository, times(1)).saveProduct(0, dbPage);
    }

    @Disabled
    @Test
    @DisplayName("redis에 저장된 데이터가 있을 때, 상품 전체 조회 가능한가")
    void getAllProducts_Test_Yes_Redis() {
        //given
        PageRequest pageRequest = PageRequest.of(0, 10);
        AllProductResponseDto product = mock(AllProductResponseDto.class);
        Page<AllProductResponseDto> dbPage = new PageImpl<>(Arrays.asList(product), pageRequest, 1);

        when(allProductRedisRepository.getProduct(0)).thenReturn(dbPage);
        when(allProductRedisRepository.getProduct(0)).thenReturn(dbPage);

        //when
        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);

        //then
        assertThat(result).isEqualTo(dbPage);
    }


    @Test
    @DisplayName("상품이 없을 때 빈 리스트가 나오는가")
    void getAllProducts_Test_No_Data() {
        PageRequest pageRequest = PageRequest.of(0, 1);
        //when
        when(productRepository.findAllDto(pageRequest, 0L)).thenReturn(new PageImpl<>(Collections.emptyList(), pageRequest, 0));
        Page<AllProductResponseDto> result = productService.getAllProducts(pageRequest);

        //then
        assertThat(result.getContent().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("특정 상품 조회가 가능한가")
    void getDetailProduct_Test_Success() {
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
    void getDetailProduct_Test_Failed() {

        //when
        when(productRepository.findById(2L)).thenThrow(ResponseStatusException.class);

        //then
        assertThatThrownBy(
                () -> productService.getDetailProduct(2L)
        ).isInstanceOf(ResponseStatusException.class);
    }



    @Test
    @DisplayName("상품 검색 테스트 성공")
    void searchAllProducts_Test() {
        //given
        ProductSearchCondition productSearchCondition = ProductSearchCondition.builder()
                .keyword("나이키")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<AllProductResponseDto> content = Collections.singletonList(new AllProductResponseDto(
                0L,
                "나이키",
                "",
                1,
                "",
                ""));

        Page<AllProductResponseDto> product = PageableExecutionUtils.getPage(content, pageRequest, () -> 0L);
        when(productsSearchRepository.searchAllProductsUsingFullText(productSearchCondition, pageRequest)).thenReturn(product);

        //when
        Page<AllProductResponseDto> products = productService.searchAllProducts(productSearchCondition, pageRequest);

        //then
        assertThat(products.getContent()).extracting("productName").containsExactly("나이키");
    }

    @Test
    @DisplayName("상품 검색 테스트 데이터 없을때")
    void searchAllProducts_Test_No_Data() {
        //given
        ProductSearchCondition productSearchCondition = ProductSearchCondition.builder()
                .keyword("fdsagfadsfdsafdsafd")
                .build();
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<AllProductResponseDto> content = new ArrayList<>();

        Page<AllProductResponseDto> product = PageableExecutionUtils.getPage(content, pageRequest, () -> 0L);
        when(productsSearchRepository.searchAllProductsUsingFullText(productSearchCondition, pageRequest)).thenReturn(product);

        //when
        Page<AllProductResponseDto> products = productService.searchAllProducts(productSearchCondition, pageRequest);

        //then
        assertThat(products.getContent().size()).isEqualTo(0);
    }

    @Test
    @DisplayName("아이디로 상품 조회했을 때, 상품이 없다면 예외 발생")
    void testGetDetailProduct_throwing_exception() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        ResponseStatusException exception = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> productService.getDetailProduct(any()));

        assertThat(exception.getMessage()).isEqualTo("400 BAD_REQUEST \"데이터가 존재하지 않습니다. 사유 : 해당 상품\"");
    }

    @Disabled
    @Test
    @DisplayName("최초의 상품 상세에 들어온다면 상품 재고를 캐시에 저장해야 한다.")
    void testGetDetailProduct_noCurrentQuantity_in_redis() {
        //given
        DetailProductResponseDto DtoMock = mock(DetailProductResponseDto.class);

        Product product = Product.builder()
                .productName("test")
                .status(OpenRunStatus.OPEN)
                .currentQuantity(10)
                .build();
        //when
        when(openRunProductRedisRepository.getCurrentQuantityCount(any())).thenReturn(null);
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        DetailProductResponseDto result = productService.getDetailProduct(anyLong());

        //then
        if (OpenRunStatus.OPEN.equals(product.getStatus())) {
            verify(openRunProductRedisRepository, times(1)).saveCurrentQuantityCount(anyLong(), any());
            verify(openRunProductRedisRepository, atLeast(1)).getCurrentQuantityCount(anyLong());
        }
        assertThat(result).isInstanceOf(DtoMock.getClass());
    }

    @Disabled
    @Test
    @DisplayName("상품 상세 조회시 이미 최초 재고가 캐시에 반영되어 있다면, 중간에 상품 재고를 캐시에 저장하면 안된다.")
    void testGetDetailProduct_yesCurrentQuantity_in_redis() {
        //given
        DetailProductResponseDto DtoMock = mock(DetailProductResponseDto.class);

        Product product = Product.builder()
                .productName("test")
                .status(OpenRunStatus.OPEN)
                .currentQuantity(10)
                .build();
        //when
        when(openRunProductRedisRepository.getCurrentQuantityCount(any())).thenReturn(10);
        when(productRepository.findById(any())).thenReturn(Optional.of(product));
        DetailProductResponseDto result = productService.getDetailProduct(anyLong());

        //then
        if (OpenRunStatus.OPEN.equals(product.getStatus())) {
            verify(openRunProductRedisRepository, never()).saveCurrentQuantityCount(anyLong(), any());
            verify(openRunProductRedisRepository, times(1)).getCurrentQuantityCount(anyLong());
        }
        assertThat(result).isInstanceOf(DtoMock.getClass());
    }


    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    @DisplayName("인기 상품 조회 테스트")
    void testGetTopCountProducts(long topN) {
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

        OngoingStubbing<List<Product>> when = when(productRepository.findTopCountProduct(topN));
        if (topN == 1) {
            when.thenReturn(Arrays.asList(product3));
        } else if (topN == 2) {
            when.thenReturn(Arrays.asList(product3, product2));
        } else {
            when.thenReturn(Arrays.asList(product3, product2, product1));
        }

        List<OpenRunProductResponseDto> result = productService.getTopCountProducts(topN);

        assertThat(result.size()).isEqualTo(topN);
        if (topN == 1) {
            assertThat(result).extracting("productName").containsExactly("test3");
        } else if (topN == 2) {
            assertThat(result).extracting("productName").containsExactly("test3", "test2");
        } else {
            assertThat(result).extracting("productName").containsExactly("test3", "test2", "test1");
        }
    }

    @Disabled
    @Test
    @DisplayName("오픈런 상품 조회 테스트")
    public void testGetOpenRunProducts_Redis_no() {
        // give
        //0. 상품이 존재해야 한다.
        OpenRunProductResponseDto product1 = new OpenRunProductResponseDto(
                1L,
                "test1",
                "image1",
                1000,
                "mall1",
                "cate1");

        OpenRunProductResponseDto product2 = new OpenRunProductResponseDto(
                2L,
                "test2",
                "image2",
                1000,
                "mall2",
                "cate2");

        //1. pageable 객체를 받아온다.
        PageRequest pageRequest = PageRequest.of(0, 16);

        // when
        when(openRunProductRedisRepository.getProduct(anyInt())).thenReturn(null);
        when(openRunProductRedisRepository.getProductCount()).thenReturn(Optional.of(2L));

        PageImpl<OpenRunProductResponseDto> dataInDB = new PageImpl<>(
                Arrays.asList(product1, product2),
                pageRequest,
                2
        );

        when(productRepository.findOpenRunProducts(any(), any(), anyLong())).thenReturn(dataInDB);
        Page<OpenRunProductResponseDto> result = productService.getOpenRunAllProducts(pageRequest);


        // then
        assertThat(result).isEqualTo(dataInDB);
        assertThat(result.getContent()).extracting("productName").contains("test1", "test2");
        verify(openRunProductRedisRepository, times(1)).saveProduct(0, result);
    }
    @Disabled
    @Test
    @DisplayName("오픈런 상품 조회 테스트")
    public void testGetOpenRunProductTest_Redis_yes() {
        // give
        ArrayList<OpenRunProductResponseDto> products = new ArrayList<>();
        for (long i = 0; i < 20; i++) {
            products.add(new OpenRunProductResponseDto(
                    (i + 1),
                    "test" + i,
                    "image" + i,
                    1000,
                    "mall" + i,
                    "cate" + i)
            );
        }


        PageRequest pageRequest = PageRequest.of(1, 16);

        PageImpl<OpenRunProductResponseDto> dataInRedis = new PageImpl<>(
                products.stream()
                        .filter(a -> a.id() > 16L)
                        .toList(),
                pageRequest,
                products.size()
        );

        // when
        when(openRunProductRedisRepository.getProduct(anyInt())).thenReturn(dataInRedis);
        Page<OpenRunProductResponseDto> result = productService.getOpenRunAllProducts(pageRequest);

        // then
        verify(openRunProductRedisRepository, only()).getProduct(1);
        verify(openRunProductRedisRepository, never()).getProductCount();
        verify(productRepository, never()).findOpenRunProducts(OpenRunStatus.OPEN, pageRequest, (long) products.size());
        Assertions.assertAll(
                () -> assertThat(result.getTotalPages()).isEqualTo(2),
                () -> assertThat(result.getTotalElements()).isEqualTo(20),
                () -> assertThat(result.getContent().size()).isEqualTo(4)
        );
    }

}