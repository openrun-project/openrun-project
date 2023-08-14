package com.project.openrun.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageProductResponseDto<T> {
    private List<T> content;
    private int number;
    private int totalPages;
    private int size;
    private long totalElements;

}
