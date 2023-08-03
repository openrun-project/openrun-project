package com.project.openrun.global.exception;

import com.project.openrun.global.exception.type.OrderErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class OrderException extends RuntimeException{

    private final OrderErrorCode orderErrorCode;

    public HttpStatus getHttpStatus() {
        return orderErrorCode.getHttpStatus();
    }

    public String getErrorMsg() {
        return orderErrorCode.getErrorMsg();
    }

}
