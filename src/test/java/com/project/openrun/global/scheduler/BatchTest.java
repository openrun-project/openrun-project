//package com.project.openrun.global.scheduler;
//
//import com.project.openrun.product.entity.OpenRunStatus;
//import com.project.openrun.product.entity.Product;
//import com.project.openrun.product.repository.ProductRepository;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.JobExecution;
//import org.springframework.batch.core.JobParameters;
//import org.springframework.batch.core.JobParametersBuilder;
//import org.springframework.batch.test.JobLauncherTestUtils;
//import org.springframework.batch.test.context.SpringBatchTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBatchTest
//@SpringBootTest
//public class BatchTest {
//
//    @Autowired
//    private JobLauncherTestUtils jobLauncherTestUtils;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//
//    @BeforeEach
//    public void setUp() {
//        // given
//        //LocalDateTime yesterday = LocalDate.now().plusDays(-1).atStartOfDay();//어제
//        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
//        System.out.println("yesterday = " + yesterday);
//
//        //LocalDateTime today = LocalDate.now().atStartOfDay();//오늘
//        LocalDateTime today = LocalDateTime.now();
//        System.out.println("today = " + today);
//
//        //LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();//내일
//        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
//        System.out.println("tomorrow = " + tomorrow);
//
//
//        //어제 오픈한것 상태 : Open
//        Product product1 = Product.builder()
//                .productName("test")
//                .price(1000)
//                .wishCount(0)
//                .mallName("test mall")
//                .totalQuantity(30)
//                .currentQuantity(30)
//                .category("category")
//                .productImage("test Image")
//                .eventStartTime(yesterday)
//                .status(OpenRunStatus.OPEN)
//                .build();
//
//        //오늘 오픈할 것 : 상태 : Wating
//        Product product2 = Product.builder()
//                .productName("test")
//                .price(1000)
//                .wishCount(0)
//                .mallName("test mall")
//                .totalQuantity(30)
//                .currentQuantity(30)
//                .category("category")
//                .productImage("test Image")
//                .eventStartTime(today)
//                .status(OpenRunStatus.WAITING)
//                .build();
//
//        //when
//        productRepository.save(product1);
//        productRepository.save(product2);
//
//    }
//
//    @Test
//    public void testProductJob() throws Exception {
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addLong("time", System.currentTimeMillis())
//                .toJobParameters();
//
//        // Launch the job
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
//
//        // Check if the job completed successfully
//        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
//
//        Product product1 = productRepository.findById(1L).orElseThrow();
//        Product product2 = productRepository.findById(2L).orElseThrow();
//
//
//        Assertions.assertThat(product1.getStatus()).isEqualTo(OpenRunStatus.CLOSE);
//        Assertions.assertThat(product2.getStatus()).isEqualTo(OpenRunStatus.OPEN);
//
//    }
//}
//
//
