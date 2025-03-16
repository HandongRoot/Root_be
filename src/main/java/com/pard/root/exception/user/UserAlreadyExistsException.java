package com.pard.root.exception.user;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String email) {
        super("이미 가입된 이메일입니다: " + email, HttpStatus.CONFLICT);
    }
}