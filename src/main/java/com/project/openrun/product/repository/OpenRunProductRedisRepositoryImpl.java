package com.project.openrun.product.repository;

import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.dto.PageProductResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OpenRunProductRedisRepositoryImpl implements CacheRedisRepository<OpenRunProductResponseDto> {

    public static final String CACHE_OPEN_RUN_PRODUCT_KEY = "OPEN_PRODUCT";
    public static final String CACHE_OPEN_RUN_PRODUCT_COUNT_KEY = "OPEN_RUN_PRODUCT_COUNT";
    public static final String CURRENT_QUANTITY_COUNT_KEY = "CURRENT_QUANTITY_COUNT";

    private final RedisTemplate<String, PageProductResponseDto> productRedisTemplate;
    private final RedisTemplate<String, String> redisCountTemplate;

    private final RedisTemplate<String, Integer> productCurrentQuantityCountTemplate;




    @Override
    public void saveProduct(int subKey, Page<OpenRunProductResponseDto> products) {

        productRedisTemplate.opsForValue().set(createKey(subKey), new PageProductResponseDto<>(
                products.getContent()
                , products.getNumber()
                , products.getTotalPages()
                , products.getSize()
                , products.getTotalElements()), getTTL());
    }

    @Override
    public Page<OpenRunProductResponseDto> getProduct(int subKey) {
        PageProductResponseDto result = productRedisTemplate.opsForValue().get(createKey(subKey));
        return result != null ? new PageImpl<>(result.getContent(), PageRequest.of(result.getNumber(), result.getSize()), result.getTotalElements()) : null;
    }

    @Override
    public void saveProductCount(Long count){
        redisCountTemplate.opsForValue().set(CACHE_OPEN_RUN_PRODUCT_COUNT_KEY, String.valueOf(count));
    }

    @Override
    public Optional<Long> getProductCount(){
        String count = redisCountTemplate.opsForValue().get(CACHE_OPEN_RUN_PRODUCT_COUNT_KEY);
        Long result = count == null ? null : Long.parseLong(count);

        return Optional.ofNullable(result);
    }



    @Override
    public void saveCurrentQuantityCount(Long subKey, Integer count) {
        productCurrentQuantityCountTemplate.opsForValue().set(createOrderKey(subKey), count, getTTL());
    }



    @Override
    public Integer getCurrentQuantityCount(Long subKey) {
        return productCurrentQuantityCountTemplate.opsForValue().get(createOrderKey(subKey));
    }

    //증가 - 반품
    @Override
    public void increaseQuantity(Long subKey, Integer count) {
        productCurrentQuantityCountTemplate.opsForValue().increment(createOrderKey(subKey), count);
    }

    //감소 - 구매
    @Override
    public Long decreaseQuantity(Long subKey, Integer count) {
        return productCurrentQuantityCountTemplate.opsForValue().decrement(createOrderKey(subKey), count);
    }


    private String createKey(int subKey) {
        return CACHE_OPEN_RUN_PRODUCT_KEY + ":" + subKey;
    }

    private String createOrderKey(Long subKey){
        return CURRENT_QUANTITY_COUNT_KEY+":"+subKey;
    }



    private static Duration getTTL() {
        LocalDateTime now = LocalDateTime.now();
        long totalSecondsUntilMidnight = Duration.between(now, now.toLocalDate().atStartOfDay().plusDays(1)).getSeconds();
        Duration time = Duration.ofSeconds(totalSecondsUntilMidnight);
        return time;
    }
}

