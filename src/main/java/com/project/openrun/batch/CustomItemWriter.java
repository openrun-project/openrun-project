package com.project.openrun.batch;

import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.repository.CacheRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomItemWriter implements ItemWriter<OpenRunProductResponseDto>, StepExecutionListener {

    private final CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;
    private static final int PAGE_SIZE = 16;
    private ExecutionContext executionContext;
    private int currentPage;


    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("CustomItemWriter :  getExecutionContext ");
        executionContext = stepExecution.getJobExecution().getExecutionContext();
    }

    @Override
    public void write(Chunk<? extends OpenRunProductResponseDto> chunk) throws Exception {

        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE);
        Page<OpenRunProductResponseDto> page = new PageImpl<>(new ArrayList<>(chunk.getItems()), pageable, executionContext.getLong("count"));
        openRunProductRedisRepository.saveProduct(currentPage, page);
        increaseCurrentPage();
    }

    private void increaseCurrentPage() {
        currentPage++;
    }
}
