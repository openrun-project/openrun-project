package com.project.openrun.product.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


public record NaverDto(
        @JsonProperty("items")
        List<NaverItemResponseDto> naverItemResponseDtoList
){}
