package com.project.openrun.global.scheuler;

import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateProductStatus() throws InterruptedException{

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

}
