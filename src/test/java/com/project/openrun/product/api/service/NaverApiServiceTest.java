//package com.project.openrun.product.api.service;
//
//import com.project.openrun.global.exception.NaverApiException;
//import com.project.openrun.product.api.dto.CreateDataRequestDto;
//import com.project.openrun.product.repository.ProductRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//class NaverApiServiceTest {
//
//    @Autowired
//    private NaverApiService naverApiService;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Test
//    @DisplayName("10개의 상품이 잘 들어가는지 확인하기")
//    public void test1() {
//        // given
//        CreateDataRequestDto requestDto = new CreateDataRequestDto("상의", 10, 1);
//
//        // when
//        naverApiService.createItemForNaverApi(requestDto);
//        long count = productRepository.count();
//
//        // then
//        Assertions.assertThat(count).isEqualTo(10);
//
//    }
//
//    @Test
//    @DisplayName("하나도 데이터를 못 가지고 왔을 때, 에러를 던지는지 확인하기")
//    void test2() {
//        // given
//        CreateDataRequestDto requestDto = new CreateDataRequestDto("", 10, 1);
//
//        // then
//        Assertions.assertThatThrownBy(
//                        () -> naverApiService.createItemForNaverApi(requestDto)
//                ).isInstanceOf(NaverApiException.class);
//    }
//}