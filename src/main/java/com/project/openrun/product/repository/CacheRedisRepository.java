package com.project.openrun.product.repository;

import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CacheRedisRepository<T> {


    void saveProductCount(Long count);
    Optional<Long> getProductCount();

}
