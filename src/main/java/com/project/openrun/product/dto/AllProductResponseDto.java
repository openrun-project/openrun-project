package com.project.openrun.product.dto;

public record AllProductResponseDto(
        Long id,
        String productName,
        String productImage,
        Integer price,
        String mallName,
        String category

) {}
