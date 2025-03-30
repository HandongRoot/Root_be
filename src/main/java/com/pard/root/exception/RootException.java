package com.pard.root.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RootException extends ResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
        return new ResponseEntity<>(ExceptionResponse.builder()
                .httpStatus(e.getExceptionCode().getHttpStatus())
                .message(e.getExceptionCode().getMessage())
                .build(),e.getExceptionCode().getHttpStatus());
    }
}
