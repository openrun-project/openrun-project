package com.project.openrun.batch;


import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
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
    private final EntityManagerFactory entityManagerFactory;
    private final CustomItemWriter customItemWriter;
    private final RetryTemplate retryTemplate;

    @Bean
    public Job productJob(
            JobRepository jobRepository,
            Step openRunProductUpdateStep,
            Step openRunCountStep,
            Step openRunSaveRedisStep,
            Step saveAllProductCountStep) {
        return new JobBuilder("productJob", jobRepository)
                .start(openRunProductUpdateStep)
                .on("FAILED").to(openRunCountStep)
                .from(openRunProductUpdateStep).on("*").to(openRunCountStep)

                .from(openRunCountStep)
                .on("FAILED").to(openRunSaveRedisStep)
                .from(openRunCountStep).on("*").to(openRunSaveRedisStep)

                .from(openRunSaveRedisStep)
                .on("FAILED").to(saveAllProductCountStep)
                .from(openRunSaveRedisStep).on("*").to(saveAllProductCountStep)

                .end()
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
    public Step openRunSaveRedisStep(JobRepository jobRepository,
                                     PlatformTransactionManager platformTransactionManager) {

        return new StepBuilder("openRunSaveRedisStep", jobRepository)
                .<Product, OpenRunProductResponseDto>chunk(16, platformTransactionManager)
                .reader(openRunSaveRedisItemReader())
                .processor(openRunSaveRedisItemProcessor())
                .writer(customItemWriter)
                .faultTolerant()
                .retryLimit(3)
                .retry(IllegalArgumentException.class)
                .skipLimit(3)
                .skip(Exception.class)
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

        return (contribution, chunkContext) ->
                retryTemplate.execute(retry -> {
                            LocalDateTime yesterday = LocalDate.now().plusDays(-1).atStartOfDay();//어제
                            LocalDateTime today = LocalDate.now().atStartOfDay();//오늘
                            LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();//내일

                            productRepository.updateProductStatus(yesterday, today, OpenRunStatus.CLOSE, OpenRunStatus.OPEN);//OPEN => CLOSE
                            productRepository.updateProductStatus(today, tomorrow, OpenRunStatus.OPEN, OpenRunStatus.WAITING);// WAITING => OPEN

                            return RepeatStatus.FINISHED;
                        }, context -> {
                            log.error("Failed after 3 retries!");
                            return null;
                        }
                );
    }


    @Bean
    public Tasklet openRunCountStepTasklet() {

        return (contribution, chunkContext) ->
                retryTemplate.execute(retry -> {
                    Long count = productRepository.countByStatus(OpenRunStatus.OPEN);
                    openRunProductRedisRepository.saveProductCount(count);

                    // count 값 BATCH JOB EXECUTION CONTEXT 에 넣어주기.
                    chunkContext.getStepContext()
                            .getStepExecution()
                            .getJobExecution()
                            .getExecutionContext()
                            .put("count", count);

                    return RepeatStatus.FINISHED;
                }, context -> {
                    log.error("Failed after 3 retries!");
                    return null;
                });
    }

    @Bean
    public JpaCursorItemReader<Product> openRunSaveRedisItemReader() {

        return new JpaCursorItemReaderBuilder<Product>()
                .name("openRunSaveRedisItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Product p where status= 'OPEN' order by id desc")
                .build();
    }

    @Bean
    public ItemProcessor<Product, OpenRunProductResponseDto> openRunSaveRedisItemProcessor() {
        return item -> new OpenRunProductResponseDto(
                item.getId(),
                item.getProductName(),
                item.getProductImage(),
                item.getPrice(),
                item.getMallName(),
                item.getCategory()
        );
    }

    @Bean
    public Tasklet saveAllProductCountStepTasklet() {

        return (contribution, chunkContext) ->

                retryTemplate.execute(retry -> {
                    Long count = productRepository.count();
                    allProductRedisRepository.saveProductCount(count);

                    return RepeatStatus.FINISHED;
                }, context -> {
                    log.error("Failed after 3 retries!");
                    return RepeatStatus.FINISHED;
                });
    }
}


