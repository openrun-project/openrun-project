package com.project.openrun.wish.dto;

public record WishProductResponseDto(
        Long id,
        String productName,
        Integer price,
        String mallName,
        String productImage

) {}
