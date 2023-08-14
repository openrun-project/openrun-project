package com.project.openrun.product.dto;

import com.project.openrun.product.entity.OpenRunStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductSearchCondition {
    private final String keyword;
    private final String category;
    private final OpenRunStatus status;
    private final Integer lprice;
    private final Integer gprice;
    private final String sortBy;
    private final Boolean isAsc;

}
