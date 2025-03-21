package com.pard.root.exception;

import org.springframework.http.HttpStatus;

public interface BaseException {
    HttpStatus getStatus();
    String getMessage();
}
