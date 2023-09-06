package com.project.openrun.orders.dto;

import com.project.openrun.orders.entity.OrderStatus;

import java.time.LocalDateTime;

public record OrderResponseDto(
        Long id,
        String productName,
        Integer price,
        String mallName,
        Integer count,
        LocalDateTime modifiedAt,
        OrderStatus orderStatus

) {
}
