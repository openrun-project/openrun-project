package com.project.openrun.product.dto;


import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class AllProductResponseDto {
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
