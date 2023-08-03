package com.project.openrun.product.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


public record NaverItemResponseDto (
    @JsonProperty("title")
    String productName,

    String image,

    @JsonProperty("lprice")
    String price,

    String mallName
){}
