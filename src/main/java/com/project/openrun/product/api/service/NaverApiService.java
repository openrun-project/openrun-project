package com.project.openrun.product.api.service;


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
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.project.openrun.global.exception.type.ErrorCode.NO_SEARCH_DATA;
import static com.project.openrun.global.exception.type.ErrorCode.WRONG_INPUT;

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

    // 상품 카테고리
    private static final HashMap<String, ArrayList<String>> PRODUCT_ITEMS;
    private static final String[] ITER_STRING;


    static {
        PRODUCT_ITEMS = new HashMap<>();
        // 상의, 하의, 신발, 가방, 모자, 노트북, 스마트폰, 가전, 향수, ...
        PRODUCT_ITEMS.put("상의", new ArrayList<>(Arrays.asList("나이키", "아디다스"/*, "발랜시아가", "빈폴", "토미힐피거", "버버리", "톰브라운", "구찌"*/))); // 8
//        PRODUCT_ITEMS.put("하의", new ArrayList<>(Arrays.asList("나이키", "아디다스", "토미힐피거", "버버리", "톰브라운", "구찌", "리바이스", "까스텔바작"))); // 8
//        PRODUCT_ITEMS.put("신발", new ArrayList<>(Arrays.asList("나이키", "아디다스", "오프화이트", "발랜시아가", "언더아머", "토미힐피거", "닥터마틴", "톰브라운", "구찌", "뉴발란스", "푸마", "마르지엘라"))); // 12
//        PRODUCT_ITEMS.put("가방", new ArrayList<>(Arrays.asList("프라다", "구찌", "에르메스", "나이키", "아디다스", "루이비통", "샤넬", "디올", "셀린느", "입생로랑")));//10
//        PRODUCT_ITEMS.put("향수", new ArrayList<>(Arrays.asList("딥티크", "샤넬", "바이레도", "조 말론 런던", "르라보", "DIOR", "랑방", "클린", "입생로랑", "불가리", "끌로에", "존바바토스"))); // 12
//        PRODUCT_ITEMS.put("노트북", new ArrayList<>(Arrays.asList("LG전자", "삼성전자", "애플", "MSI", "레노버", "HP", "ASUS", "한성"))); // 8
//        PRODUCT_ITEMS.put("스마트폰", new ArrayList<>(Arrays.asList("LG전자", "삼성전자", "애플", "화웨이"))); // 4
//        PRODUCT_ITEMS.put("모자", new ArrayList<>(Arrays.asList("나이키", "아디다스", "뉴 에라", "스텟슨", "구린 브로스", "미첼 앤 네스", "베일리", "허프", "틸리"))); // 9
        ITER_STRING = new String[]{"[신상]", "[인기]", "[중고]", "[해외배송]", "", "[무료배송]", "[빠른배송]", "[핫딜]", "[정품]", "[최시우 상품]"};
    }


    public void createItemForNaverApi(CreateDataRequestDto requestDto) {
        // 첫 for 문 -> display:100, start:1  /  display:100, start:101 / display:100, start:201  /  display:100, start:301 / ..  /  display:100, start:901 / display:100 , start:1000
        int display = 100;
        for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : PRODUCT_ITEMS.entrySet()) {

            String category = stringArrayListEntry.getKey();

            for (String value : stringArrayListEntry.getValue()) {
                int i = 1;
                while (true) {

                    if (i > 1000) {
                        break;
                    }

                    URI uri = naverUriBuilderService.buildUriByQueryAndDisplayAndStart(value + category, display, i);

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
                        throw new ResponseStatusException(WRONG_INPUT.getStatus(), WRONG_INPUT.getMessageTemplate());
                    }

                    if (ObjectUtils.isEmpty(naverDto.naverItemResponseDtoList())) {
                        log.error("[NaverApiService createItemForNaverApi] no itemResponseDtoList");
                        throw new ResponseStatusException(NO_SEARCH_DATA.getStatus(),NO_SEARCH_DATA.getMessageTemplate());
                    }

                    List<Product> products = new ArrayList<>();

                    naverDto.naverItemResponseDtoList().forEach((dto) -> {
                        Arrays.stream(ITER_STRING).forEach((string) -> {
                            for (int j = 1; j <= 10; j++) {

                                Product newProduct = Product.builder()
                                        .price(Integer.valueOf(dto.price()))
                                        .productImage(dto.image())
                                        .productName(string + dto.productName() + " " + j)
                                        .category(category)
                                        .mallName(dto.mallName())
                                        .currentQuantity(30)    // 여기는 메서드로 랜덤하게 넣어주는 방향도 고려
                                        .eventStartTime(setDate())
                                        .totalQuantity(30)
                                        .wishCount(0)
                                        .status(OpenRunStatus.WAITING)
                                        .build();

                                products.add(newProduct);
                            }
                        });


                    });
                    // bulk 연산 적용 테스트 확인 필요
                    productRepository.saveAll(products);

                    i += 100;

                }
            }
        }

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
