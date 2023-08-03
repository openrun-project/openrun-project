package com.project.openrun.product.dto;


import java.time.LocalDateTime;


public record AllProductResponseDto (
    Long id,
    String productName,
    String productImage,
    Integer price,
    String mallName,
    Integer currentQuantity,
    LocalDateTime eventStartTime,
    String category,
    Integer totalQuantity,
    Integer wishCount
){}
