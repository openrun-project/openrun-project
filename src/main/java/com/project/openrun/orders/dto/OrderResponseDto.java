package com.project.openrun.orders.dto;

public record OrderResponseDto(
        Long id,
        String productName,
        Integer price,
        String mallName,
        Integer count
) {}
