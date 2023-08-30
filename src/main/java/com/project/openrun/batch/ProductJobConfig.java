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
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ProductJobConfig {

    private final ProductRepository productRepository;
    private final CacheRedisRepository<AllProductResponseDto> allProductRedisRepository;
    private final CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;

    private final EntityManagerFactory entityManagerFactory;
    private int currentPage;
    private long count;

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
    public Step openRunSaveRedisStep(JobRepository jobRepository
            , PlatformTransactionManager platformTransactionManager) {

        return new StepBuilder("openRunSaveRedisStep", jobRepository)
                .<Product, OpenRunProductResponseDto>chunk(16, platformTransactionManager)
                .reader(openRunSaveRedisItemReader())
                .writer(openRunSaveRedisItemWriter())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        currentPage = 0;
                        count = stepExecution.getJobExecution().getExecutionContext().getLong("count");
                    }
                })
                .listener(new ChunkListener() {
                    @Override
                    public void afterChunk(ChunkContext context) {
                        currentPage++;
                    }
                })
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
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
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 최대 3회 재시도
        retryTemplate.setRetryPolicy(retryPolicy);

        return (contribution, chunkContext) -> {
            return retryTemplate.execute(retry -> {
                        LocalDateTime yesterday = LocalDate.now().plusDays(-1).atStartOfDay();//어제
                        LocalDateTime today = LocalDate.now().atStartOfDay();//오늘
                        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();//내일

                        productRepository.updateProductStatus(yesterday, today, OpenRunStatus.CLOSE, OpenRunStatus.OPEN);//OPEN => CLOSE
                        productRepository.updateProductStatus(today, tomorrow, OpenRunStatus.OPEN, OpenRunStatus.WAITING);// WAITING => OPEN

                        return RepeatStatus.FINISHED;
                    }, context -> {
                        log.error("Failed after 3 retries!");
                        return RepeatStatus.FINISHED;
                    }
            );
        };
    }


    @Bean
    public Tasklet openRunCountStepTasklet() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 최대 3회 재시도
        retryTemplate.setRetryPolicy(retryPolicy);

        return (contribution, chunkContext) -> {

            return retryTemplate.execute(retry -> {
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
                return RepeatStatus.FINISHED;
            });

        };
    }

    @Bean
    public JpaPagingItemReader<Product> openRunSaveRedisItemReader() {

        return new JpaPagingItemReaderBuilder<Product>()
                .name("openRunSaveRedisItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select p from Product p where status= 'OPEN' order by id desc")
                .pageSize(16)
                .build();
    }
//    @Bean
//    public JpaCursorItemReader<Product> openRunSaveRedisItemReader() {
//
//        return new JpaCursorItemReaderBuilder<Product>()
//                .name("openRunSaveRedisItemReader")
//                .entityManagerFactory(entityManagerFactory)
//                .queryString("select p from Product p where status= 'OPEN' order by id desc")
//                .build();
//    }

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
    public ItemWriter<OpenRunProductResponseDto> openRunSaveRedisItemWriter() {

        return chunk -> {
            Pageable pageable = PageRequest.of(currentPage, 16);
            Page<OpenRunProductResponseDto> page = new PageImpl<>(new ArrayList<>(chunk.getItems()), pageable, count);
            openRunProductRedisRepository.saveProduct(currentPage, page);
        };
    }

    @Bean
    public Tasklet saveAllProductCountStepTasklet() {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(3); // 최대 3회 재시도
        retryTemplate.setRetryPolicy(retryPolicy);

        return (contribution, chunkContext) -> {

            return retryTemplate.execute(retry -> {
                Long count = productRepository.count();
                allProductRedisRepository.saveProductCount(count);

                return RepeatStatus.FINISHED;
            }, context -> {
                log.error("Failed after 3 retries!");
                return RepeatStatus.FINISHED;
            });
        };
    }


//    @Bean
//    public Tasklet openRunSaveRedisStepTasklet() {
//        return (contribution, chunkContext) -> {
//            // count 값 BATCH JOB EXECUTION CONTEXT 에서 꺼내오기
//            ExecutionContext jobExecutionContext = chunkContext.getStepContext()
//                    .getStepExecution()
//                    .getJobExecution()
//                    .getExecutionContext();
//            Long count = jobExecutionContext.getLong("count");
//
//            // 저장 로직
//            long totalPages = count / 16; // 17 => 1.xx  0, 1
//
//            for (int i = 0; i <= totalPages; i++) {
//                Pageable pageable = PageRequest.of(i, 16);
//                openRunProductRedisRepository.saveProduct(i, productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageable, count));
//            }
//
//            return RepeatStatus.FINISHED;
//        };
//    }

}


