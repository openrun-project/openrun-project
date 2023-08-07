package com.project.openrun.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorCode {

    NO_MEMBER(HttpStatus.BAD_REQUEST, "사용자를 찾을수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일입니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "이메일과 비밀번호를 확인해주세요."),
    ;


    private final HttpStatus httpStatus;
    private final String errorMsg;
}
