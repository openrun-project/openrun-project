package com.project.openrun.global.scheuler;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.repository.CacheRedisRepository;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j(topic = "ProductScheduler")
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductRepository productRepository;
    private final CacheRedisRepository<AllProductResponseDto> allProductRedisRepository;
    private final CacheRedisRepository<OpenRunProductResponseDto> openRunProductRedisRepository;


    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void schedulingOpenRunProduct() throws InterruptedException{
        updateProductStatus();
        Long count = saveOpenRunCount();
        saveAllProductCount();
        saveOpenRunRedisCache(count);
    }

    private void updateProductStatus(){
        LocalDateTime yesterday = LocalDate.now().plusDays(-1).atStartOfDay();//어제
        LocalDateTime today = LocalDate.now().atStartOfDay();//오늘
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();//내일

        try{
            productRepository.updateProductStatus(yesterday, today, OpenRunStatus.CLOSE, OpenRunStatus.OPEN);//OPEN => CLOSE
            productRepository.updateProductStatus(today, tomorrow, OpenRunStatus.OPEN, OpenRunStatus.WAITING);// WAITING => OPEN
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

    private void saveOpenRunRedisCache(Long count){
        Pageable pageable = PageRequest.of(0, 16);

        Page<OpenRunProductResponseDto> openRunProducts = productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageable,count);

        for (int i = 0; i <= openRunProducts.getTotalPages(); i++) {
            pageable = PageRequest.of(i, 16);
            //openRunProductRedisRepository.saveProduct(i, productRepository.findOpenRunProducts(OpenRunStatus.OPEN, pageable,count));
        }
    }

    public void saveAllProductCount(){
        Long count = productRepository.count();
        allProductRedisRepository.saveProductCount(count);
    }

    public Long saveOpenRunCount(){
        Long count = productRepository.countByStatus(OpenRunStatus.OPEN);
        openRunProductRedisRepository.saveProductCount(count);
        return count;
    }

}
