package com.pard.root.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {
    private final BaseException baseException;

    public CustomException(BaseException baseException1) {
        this.baseException = baseException1;
    }
}