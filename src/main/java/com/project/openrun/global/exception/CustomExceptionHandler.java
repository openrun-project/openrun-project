package com.project.openrun.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class CustomExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity dataValidException(MethodArgumentNotValidException ex){
        log.error("Data Not Valid", ex);
        return ResponseEntity.status(400).build();
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<?> memberExceptionHandler(MemberException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getErrorMsg());
    }

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<?> orderExceptionHandler(OrderException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getErrorMsg());
    }

    @ExceptionHandler(NaverApiException.class)
    public ResponseEntity<?> naverApiExceptionHandler(NaverApiException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getErrorMsg());
    }

    @ExceptionHandler(ProductException.class)
    public ResponseEntity<String> productExceptionHandler(ProductException ex){
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getErrorMsg());
    }

    @ExceptionHandler(WishException.class)
    public ResponseEntity<?> wishExceptionHandler(WishException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getErrorMsg());
    }
}
