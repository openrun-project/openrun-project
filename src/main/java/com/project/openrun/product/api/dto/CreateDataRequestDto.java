package com.project.openrun.product.api.dto;

import lombok.Getter;

@Getter
public class CreateDataRequestDto {
    private String query;
    private Integer display;
    private Integer start;
}
