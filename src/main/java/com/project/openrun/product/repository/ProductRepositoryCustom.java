package com.project.openrun.product.repository;

import com.project.openrun.product.dto.AllProductResponseDto;
import com.project.openrun.product.dto.OpenRunProductResponseDto;
import com.project.openrun.product.dto.ProductSearchCondition;
import com.project.openrun.product.entity.OpenRunStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<AllProductResponseDto> searchAllProducts(ProductSearchCondition condition, Pageable pageable);

    Page<AllProductResponseDto> findAllDto(Pageable pageable,Long count);

    Page<OpenRunProductResponseDto> findOpenRunProducts(OpenRunStatus openRunStatus, Pageable pageable,Long count);

}
