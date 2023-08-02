package com.project.openrun.product.api.service;


import com.project.openrun.product.api.dto.CreateDataRequestDto;
import com.project.openrun.product.api.dto.NaverDto;
import com.project.openrun.product.api.dto.NaverItemResponseDto;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NaverApiService {

    private final RestTemplate restTemplate;
    private final NaverUriBuilderService naverUriBuilderService;
    private final ProductRepository productRepository;

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    public void createItemForNaverApi(CreateDataRequestDto requestDto) {

        URI uri = naverUriBuilderService.buildUriByQueryAndDisplayAndStart(requestDto.getQuery(), requestDto.getDisplay(), requestDto.getStart());

        // 보내줄 헤더 정보. 메타 정보
        RequestEntity<Void> voidRequestEntity = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        // 반환 결과
        NaverDto naverDto = restTemplate.exchange(voidRequestEntity, NaverDto.class).getBody();

        List<Product> products = new ArrayList<>();
        naverDto.getNaverItemResponseDtoList().forEach((dto) -> {

            Product newProduct = Product.builder()
                    .price(Integer.valueOf(dto.getPrice()))
                    .productImage(dto.getImage())
                    .productName(dto.getProductName())
                    .category(requestDto.getQuery())
                    .mallName(dto.getMallName())
                    .currentQuantity(30)    // 여기는 메서드로 랜덤하게 넣어주는 방향도 고려
                    .eventStartTime(setDate())
                    .totalQuantity(30)
                    .wishCount(0)
                    .build();

            products.add(newProduct);
        });

        productRepository.saveAll(products);

    }

    private LocalDateTime setDate() {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.of(2023, Month.DECEMBER, 31);

        LocalDate randomDate = getRandomDateBetween(from, to);

        // 랜덤 날짜에 9시 설정
        LocalDateTime dateTime = randomDate.atTime(9, 0, 0);

        System.out.println(dateTime);

        return dateTime;
    }

    private static LocalDate getRandomDateBetween(LocalDate from, LocalDate to) {
        long totalDays = ChronoUnit.DAYS.between(from, to);
        long randomDays = ThreadLocalRandom.current().nextLong(totalDays);

        return from.plusDays(randomDays);
    }
}
