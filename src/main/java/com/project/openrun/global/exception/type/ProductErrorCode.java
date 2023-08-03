package com.project.openrun.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode {

    NO_PRODUCT_SEARCH(HttpStatus.BAD_REQUEST, "해당 상품이 없습니다.");


    private final HttpStatus httpStatus;
    private final String errorMsg;
}
