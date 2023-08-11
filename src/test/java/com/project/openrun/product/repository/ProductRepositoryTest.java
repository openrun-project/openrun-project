package com.project.openrun.product.repository;

import com.project.openrun.TestConfig;
import com.project.openrun.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@DirtiesContext
@Import(TestConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("레포짓토리테스트 - 단건 조회")
    void test1() {
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

        productRepository.save(product1);

        //when
        Product result = productRepository.findById(1L).orElse(null);

        //then
        assertThat(result.getId()).isEqualTo(1L);
    }
}