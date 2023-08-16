package com.project.openrun.product.repository;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.PageProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AllProductRedisRepositoryImpl implements CacheRedisRepository<AllProductResponseDto> {

    public static final String CACHE_ALL_PRODUCT_KEY = "ALL_PRODUCT";
    public static final String CACHE_ALL_PRODUCT_COUNT_KEY = "ALL_PRODUCT_COUNT";

    private final RedisTemplate<String, PageProductResponseDto> redisTemplate;
    private final RedisTemplate<String, String> redisCountTemplate;




    @Override
    public void saveProductCount(Long count){
        redisCountTemplate.opsForValue().set(CACHE_ALL_PRODUCT_COUNT_KEY, String.valueOf(count));
    }

    @Override
    public Optional<Long> getProductCount(){
        String count = redisCountTemplate.opsForValue().get(CACHE_ALL_PRODUCT_COUNT_KEY);
        Long result = count == null ? null : Long.parseLong(count);

        return Optional.ofNullable(result);
    }

    private String createKey(int subKey) {
        return CACHE_ALL_PRODUCT_KEY + ":" + subKey;
    }
}

