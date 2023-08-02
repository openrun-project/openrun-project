package com.project.openrun.product.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class NaverItemResponseDto {
    @JsonProperty("title")
    private String productName;

    private String image;

    @JsonProperty("lprice")
    private String price;

    private String mallName;
}
