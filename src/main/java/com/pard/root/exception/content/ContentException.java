package com.pard.root.exception.content;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ContentException extends RuntimeException {
    private final HttpStatus status;

    public ContentException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
