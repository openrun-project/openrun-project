package com.project.openrun.product.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class NaverDto {

    @JsonProperty("items")
    private List<NaverItemResponseDto> naverItemResponseDtoList;
}
