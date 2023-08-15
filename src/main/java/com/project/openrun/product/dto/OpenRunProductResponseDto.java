package com.project.openrun.product.dto;

public record OpenRunProductResponseDto(
        Long id,
        String productName,
        String productImage,
        Integer price,
        String mallName,
        String category

) {}
