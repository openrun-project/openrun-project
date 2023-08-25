package com.project.openrun.product.repository;

import com.project.openrun.product.dto.DetailProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;


@Repository
@RequiredArgsConstructor
public class OpenProductDetailRedisRepository {

    public static final String CACHE_OPEN_PRODUCT_DETAIL_KEY = "OPEN_PRODUCT_DETAIL";

    private final RedisTemplate<String, DetailProductResponseDto> productDetailRedisTemplate;

    public void saveOpenProduct(int subKey, DetailProductResponseDto detailProductResponseDto){
        LocalDateTime now = LocalDateTime.now();
        long totalSecondsUntilMidnight = Duration.between(now, now.toLocalDate().atStartOfDay().plusDays(1)).getSeconds();
        Duration time = Duration.ofSeconds(totalSecondsUntilMidnight);

        //OPEN_PRODUCT_DETAIL + 상품ID
        productDetailRedisTemplate.opsForValue()
                .set(createKey(subKey), detailProductResponseDto, time);
    }

    public DetailProductResponseDto getProduct(int subKey){
        DetailProductResponseDto result = productDetailRedisTemplate.opsForValue().get(createKey(subKey));
        return result;
    }




    private String createKey(int subKey) {
        return CACHE_OPEN_PRODUCT_DETAIL_KEY + ":" + subKey;
    }
}
