package com.pard.root.exception.user;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class UserNotFoundException extends UserException {
    public UserNotFoundException(UUID userId) {
        super("해당 ID의 사용자를 찾을 수 없습니다: " + userId, HttpStatus.NOT_FOUND);
    }

    public UserNotFoundException(String userId) {
        super("해당 ID의 사용자를 찾을 수 없습니다: " + userId, HttpStatus.NOT_FOUND);
    }
}
