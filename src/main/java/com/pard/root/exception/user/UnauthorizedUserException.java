package com.pard.root.exception.user;

import org.springframework.http.HttpStatus;

public class UnauthorizedUserException extends UserException {
    public UnauthorizedUserException() {
        super("권한이 없습니다.", HttpStatus.UNAUTHORIZED);
    }
}