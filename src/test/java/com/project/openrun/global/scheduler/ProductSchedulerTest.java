package com.project.openrun.global.scheduler;

import com.project.openrun.TestConfig;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;

@DataJpaTest
@DirtiesContext
@Import(TestConfig.class)
class ProductSchedulerTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("스케줄러 테스트")
    void test(){
        //OPEN이지만 아직 시간이 안된경우 막기? 어디서?

        // given
        //LocalDateTime yesterday = LocalDate.now().plusDays(-1).atStartOfDay();//어제
        LocalDateTime yesterday = LocalDateTime.of(2023, 8, 7, 9, 0);

        //LocalDateTime today = LocalDate.now().atStartOfDay();//오늘
        LocalDateTime today = LocalDateTime.of(2023, 8, 8, 9, 0);

        //LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();//내일
        LocalDateTime tomorrow = LocalDateTime.of(2023, 8, 9, 9, 0);

        //어제 오픈한것 상태 : Open
        Product product1 = Product.builder()
                .productName("test")
                .price(1000)
                .wishCount(0)
                .mallName("test mall")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category")
                .productImage("test Image")
                .eventStartTime(yesterday)
                .status(OpenRunStatus.OPEN)
                .build();

        //오늘 오픈할 것 : 상태 : Wating
        Product product2 = Product.builder()
                .productName("test")
                .price(1000)
                .wishCount(0)
                .mallName("test mall")
                .totalQuantity(30)
                .currentQuantity(30)
                .category("category")
                .productImage("test Image")
                .eventStartTime(today)
                .status(OpenRunStatus.WAITING)
                .build();

        //when
        productRepository.save(product1);
        productRepository.save(product2);

        entityManager.flush();
        entityManager.clear();

        productRepository.updateProductStatus(yesterday, today, OpenRunStatus.CLOSE, OpenRunStatus.OPEN);//OPEN => CLOSE
        productRepository.updateProductStatus(today, tomorrow, OpenRunStatus.OPEN, OpenRunStatus.WAITING);// WAITING => OPEN

        //then
        System.out.println("productRepository.findById(1L).orElseThrow().getStatus() = " + productRepository.findById(1L).orElseThrow().getStatus());
        System.out.println("productRepository.findById(2L).orElseThrow().getStatus() = " + productRepository.findById(2L).orElseThrow().getStatus());
        Assertions.assertThat(productRepository.findById(1L).orElseThrow().getStatus()).isEqualTo(OpenRunStatus.CLOSE);
        Assertions.assertThat(productRepository.findById(2L).orElseThrow().getStatus()).isEqualTo(OpenRunStatus.OPEN);

    }
}