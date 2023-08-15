package com.project.openrun.product.repository;

import org.springframework.data.domain.Page;

public interface CachshRedisRepository<T> {

    void saveProduct(int subKey, Page<T> products);

    Page<T> getProduct(int subKey);

}
