package com.project.openrun.global.exception;

import com.project.openrun.global.exception.type.NaverApiErrorCode;
import com.project.openrun.global.exception.type.WishErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class NaverApiException extends RuntimeException {

    private final NaverApiErrorCode naverApiErrorCode;

    public HttpStatus getHttpStatus() {
        return naverApiErrorCode.getHttpStatus();
    }

    public String getErrorMsg() {
        return naverApiErrorCode.getErrorMsg();
    }
}
