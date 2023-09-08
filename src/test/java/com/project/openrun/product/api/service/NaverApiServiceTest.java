package com.project.openrun.product.api.service;

import com.project.openrun.product.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Disabled
@SpringBootTest
@Transactional
class NaverApiServiceTest {

    @Autowired
    private NaverApiService naverApiService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("10개의 상품이 잘 들어가는지 확인하기")
    public void test1() {

        // when
        naverApiService.createItemForNaverApi();
        long count = productRepository.count();

        // then
        Assertions.assertThat(count).isEqualTo(10);

    }

}