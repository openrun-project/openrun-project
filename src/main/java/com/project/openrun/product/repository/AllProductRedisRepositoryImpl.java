package com.project.openrun.product.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.PageProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class AllProductRedisRepositoryImpl implements CachshRedisRepository<AllProductResponseDto> {

    public static final String CACHE_ALL_PRODUCT_KEY = "ALL_PRODUCT";

    private final RedisTemplate<String, PageProductResponseDto> redisTemplate;


    @Override
    public void saveProduct(int subKey, Page<AllProductResponseDto> products) {
        redisTemplate.opsForValue().set(createKey(subKey), new PageProductResponseDto<>(
                products.getContent()
                , products.getNumber()
                , products.getTotalPages()
                , products.getSize()
                , products.getTotalElements()), Duration.ofHours(1));
    }

    @Override
    public Page<AllProductResponseDto> getProduct(int subKey) {
        PageProductResponseDto result = redisTemplate.opsForValue().get(createKey(subKey));
        return result != null ? new PageImpl<>(result.getContent(), PageRequest.of(result.getNumber(), result.getSize()), result.getTotalElements()) : null;
    }

    private String createKey(int subKey) {
        return CACHE_ALL_PRODUCT_KEY + ":" + subKey;
    }
}

