package com.project.openrun.product.api.service;


import com.project.openrun.global.exception.NaverApiException;
import com.project.openrun.global.exception.type.NaverApiErrorCode;
import com.project.openrun.product.api.dto.CreateDataRequestDto;
import com.project.openrun.product.api.dto.NaverDto;
import com.project.openrun.product.entity.OpenRunStatus;
import com.project.openrun.product.entity.Product;
import com.project.openrun.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestClientException;
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

        URI uri = naverUriBuilderService.buildUriByQueryAndDisplayAndStart(requestDto.query(), requestDto.display(), requestDto.start());

        // 보내줄 헤더 정보. 메타 정보
        RequestEntity<Void> voidRequestEntity = RequestEntity
                .get(uri)
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .build();

        // 반환 결과
        NaverDto naverDto;
        try {
            naverDto = restTemplate.exchange(voidRequestEntity, NaverDto.class).getBody();
        } catch (RestClientException e) {
            throw new NaverApiException(NaverApiErrorCode.WRONG_INPUT);
        }

        if (ObjectUtils.isEmpty(naverDto.naverItemResponseDtoList())) {
            log.error("[NaverApiService createItemForNaverApi] no itemResponseDtoList");
            throw new NaverApiException(NaverApiErrorCode.NO_SEARCH_DATA);
        }

        List<Product> products = new ArrayList<>();

        naverDto.naverItemResponseDtoList().forEach((dto) -> {

            Product newProduct = Product.builder()
                    .price(Integer.valueOf(dto.price()))
                    .productImage(dto.image())
                    .productName(dto.productName())
                    .category(requestDto.query())
                    .mallName(dto.mallName())
                    .currentQuantity(30)    // 여기는 메서드로 랜덤하게 넣어주는 방향도 고려
                    .eventStartTime(setDate())
                    .totalQuantity(30)
                    .wishCount(0)
                    .status(OpenRunStatus.WAITING)
                    .build();

            products.add(newProduct);
        });
        // bulk 연산 적용 테스트 확인 필요
        productRepository.saveAll(products);

    }

    private LocalDateTime setDate() {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.of(2023, Month.AUGUST, 31);

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
