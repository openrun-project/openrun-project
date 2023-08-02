package com.project.openrun.product.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class DetailProductResponseDto {
    private Long id;
    private String productName;
    private String productImage;
    private Integer price;
    private String mallName;
    private Integer currentQuantity;
    private LocalDateTime eventStartTime;
    private String category;
    private Integer totalQuantity;
    private Integer wishCount;
}
