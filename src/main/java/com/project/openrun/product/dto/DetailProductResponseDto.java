package com.project.openrun.product.dto;


import com.project.openrun.product.entity.OpenRunStatus;

import java.time.LocalDateTime;


public record DetailProductResponseDto (
        Long id,
        String productName,
        String productImage,
        Integer price,
        String mallName,
        Integer currentQuantity,
        LocalDateTime eventStartTime,
        String category,
        Integer totalQuantity,
        Integer wishCount,
        OpenRunStatus status
){}
