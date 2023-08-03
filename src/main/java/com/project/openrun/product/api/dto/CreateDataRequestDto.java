package com.project.openrun.product.api.dto;


public record CreateDataRequestDto (
    String query,
    Integer display,
    Integer start
){}
