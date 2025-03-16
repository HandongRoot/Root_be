package com.pard.root.exception.user;

import org.springframework.http.HttpStatus;

public class InvalidUserRequestException extends UserException {
    public InvalidUserRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}