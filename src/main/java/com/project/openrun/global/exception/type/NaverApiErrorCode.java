package com.project.openrun.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum NaverApiErrorCode {
    NO_SEARCH_DATA(HttpStatus.BAD_REQUEST, "Naver API로부터 전달받은 데이터가 없습니다."),
    WRONG_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다.");

    private final HttpStatus httpStatus;
    private final String errorMsg;
}
