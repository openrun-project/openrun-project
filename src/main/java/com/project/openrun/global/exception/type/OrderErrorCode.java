package com.project.openrun.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode {

    NOT_ORDER(HttpStatus.BAD_REQUEST,"주문 내역이 없습니다."),
    NOT_PRODUCT(HttpStatus.BAD_REQUEST, "해당 상품이 존재하지 않습니다."),
    NOT_USER_ORDER(HttpStatus.BAD_REQUEST, "삭제할 권한이 없습니다.");

    private final HttpStatus httpStatus;
    private final String errorMsg;
}
