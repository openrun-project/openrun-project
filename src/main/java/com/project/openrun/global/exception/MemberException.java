package com.project.openrun.global.exception;

import com.project.openrun.global.exception.type.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public class MemberException extends RuntimeException{

    private final MemberErrorCode memberErrorCode;

    public HttpStatus getHttpStatus() {
        return memberErrorCode.getHttpStatus();
    }

    public String getErrorMsg() {
        return memberErrorCode.getErrorMsg();
    }

}
