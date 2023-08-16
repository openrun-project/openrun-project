package com.project.openrun.product.repository;

import org.springframework.data.domain.Page;

import java.util.Optional;

public interface CacheRedisRepository<T> {

    void saveProduct(int subKey, Page<T> products);

    Page<T> getProduct(int subKey);

    void saveProductCount(Long count);
    Optional<Long> getProductCount();

}
