package com.project.openrun.global.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import java.text.MessageFormat;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    NOT_FOUND_DATA(HttpStatus.BAD_REQUEST,"데이터가 존재하지 않습니다. 사유 : {0}"),
    DUPLICATE_DATA(HttpStatus.BAD_REQUEST, "중복된 데이터 입니다. 사유 : {0}"),
    NOT_AUTHORIZATION(HttpStatus.BAD_REQUEST,"권한이 없습니다. 사유 : {0}"),
    INVALID_CONDITION(HttpStatus.BAD_REQUEST, "조건이 맞지 않습니다. 사유 : {0}"),

    NO_SEARCH_DATA(HttpStatus.BAD_REQUEST, "Naver API로부터 전달받은 데이터가 없습니다."),
    WRONG_INPUT(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다.");

//    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "이메일과 비밀번호를 확인해주세요."),
//    NO_MEMBER(HttpStatus.BAD_REQUEST, "사용자를 찾을수 없습니다."),
//    NO_PRODUCT_SEARCH(HttpStatus.BAD_REQUEST, "해당 상품이 없습니다."),
//    NOT_EXIST_PRODUCT(HttpStatus.BAD_REQUEST, "상품이 없습니다."),

//    ALREADY_CHOOSE_WISH(HttpStatus.BAD_REQUEST, "이미 찜한 상품입니다."),
//    NO_CHOOSE_WISH(HttpStatus.BAD_REQUEST, "찜한 상품이 아닙니다." );

    private final HttpStatus status;
    private final String messageTemplate;

    public String formatMessage(Object args) {
        return MessageFormat.format(this.messageTemplate, args);
    }

}
