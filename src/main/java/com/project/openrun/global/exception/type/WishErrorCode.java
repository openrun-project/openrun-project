package com.project.openrun.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;


@Getter
@RequiredArgsConstructor
public enum WishErrorCode {

    NOT_EXIST_PRODUCT(HttpStatus.BAD_REQUEST, "상품이 없습니다."),
    ALREADY_CHOOSE_WISH(HttpStatus.BAD_REQUEST, "이미 찜한 상품입니다."),
    NO_CHOOSE_WISH(HttpStatus.BAD_REQUEST, "찜한 상품이 아닙니다." );

    private final HttpStatus httpStatus;
    private final String errorMsg;
}
