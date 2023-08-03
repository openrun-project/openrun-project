package com.project.openrun.global.exception;

import com.project.openrun.global.exception.type.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public class ProductException extends RuntimeException{

    private final ProductErrorCode productErrorCode;

    public HttpStatus getHttpStatus() {
        return productErrorCode.getHttpStatus();
    }

    public String getErrorMsg() {
        return productErrorCode.getErrorMsg();
    }
}
