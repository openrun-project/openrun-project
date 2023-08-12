package com.project.openrun.product.dto;

public record AllProductResponseDto(
        Long id,
        String productName,
        Integer price,
        String mallName,
        String category

) {
    public void AllProductResponseDto() {
    }
}
