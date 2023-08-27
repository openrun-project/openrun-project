package com.project.openrun.batch;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProductJobConfig {

    private final ProductRepository productRepository;
    private final CacheRedisRepository<AllProductResponseDto> allProductRedisRepository;
    private final CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;

    @Bean
    public Job productJob(
            JobRepository jobRepository,
            Step openRunProductUpdateStep,
            Step openRunCountStep,
            Step openRunSaveRedisStep,
            Step saveAllProductCountStep) {
        return new JobBuilder("productJob", jobRepository)
                .start(openRunProductUpdateStep)
                .next(openRunCountStep)
                .next(openRunSaveRedisStep)
                .next(saveAllProductCountStep)
       .build();
    }


    @Bean
    public Step openRunProductUpdateStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("openRunProductUpdateStep", jobRepository)
                .tasklet(openRunUpdateStepTasklet(), platformTransactionManager)
                .build();
    }

    @Bean
    public Step openRunCountStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("openRunCountStep", jobRepository)
                .tasklet(openRunCountStepTasklet(), platformTransactionManager)
                .build();
    }


    @Bean
    public Step openRunSaveRedisStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("openRunSaveRedisStep", jobRepository)
                .tasklet(openRunSaveRedisStepTasklet(), platformTransactionManager)
                .build();
    }


    @Bean
    public Step saveAllProductCountStep(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("saveAllProductCountStep", jobRepository)
                .tasklet(saveAllProductCountStepTasklet(), platformTransactionManager)
                .build();
    }



    @Bean
    public Tasklet openRunUpdateStepTasklet() {
        return (contribution, chunkContext) -> {
            LocalDateTime yesterday = LocalDate.now().plusDays(-1).atStartOfDay();//어제
            LocalDateTime today = LocalDate.now().atStartOfDay();//오늘
            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();//내일

            try {
                productRepository.updateProductStatus(yesterday, today, OpenRunStatus.CLOSE, OpenRunStatus.OPEN);//OPEN => CLOSE
                productRepository.updateProductStatus(today, tomorrow, OpenRunStatus.OPEN, OpenRunStatus.WAITING);// WAITING => OPEN
            } catch (Exception e) {
                log.error(e.getMessage());
            }
            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public Tasklet openRunCountStepTasklet() {
        return (contribution, chunkContext) -> {

            Long count = productRepository.countByStatus(OpenRunStatus.OPEN);
            openRunProductRedisRepository.saveProductCount(count);

            // count 값 BATCH JOB EXECUTION CONTEXT 에 넣어주기.
            chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext()
                    .put("count", count);

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Tasklet openRunSaveRedisStepTasklet() {
        return (contribution, chunkContext) -> {
            // count 값 BATCH JOB EXECUTION CONTEXT 에서 꺼내오기
            ExecutionContext jobExecutionContext = chunkContext.getStepContext()
                    .getStepExecution()
                    .getJobExecution()
                    .getExecutionContext();
            Long count = jobExecutionContext.getLong("count");

            // 저장 로직
            long totalPages = count / 16; // 17 => 1.xx  0, 1

            for (int i = 0; i <= totalPages; i++) {
                Pageable pageable = PageRequest.of(i, 16);
                openRunProductRedisRepository.saveProduct(i, productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageable, count));
            }

            return RepeatStatus.FINISHED;
        };
    }


    @Bean
    public Tasklet saveAllProductCountStepTasklet() {
        return (contribution, chunkContext) -> {

            Long count = productRepository.count();
            allProductRedisRepository.saveProductCount(count);

            return RepeatStatus.FINISHED;
        };
    }

}
