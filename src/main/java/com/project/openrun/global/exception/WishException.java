package com.project.openrun.global.exception;

import com.project.openrun.global.exception.type.WishErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@RequiredArgsConstructor
public class WishException extends RuntimeException{
    private final WishErrorCode wishErrorCode;

    public HttpStatus getHttpStatus() {
        return wishErrorCode.getHttpStatus();
    }


    public String getErrorMsg() {
        return wishErrorCode.getErrorMsg();
    }

}
