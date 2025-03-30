package com.pard.root.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {
    HttpStatus getHttpStatus();
    String getMessage();
}
