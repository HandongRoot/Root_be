package com.pard.root.exception.content;

import org.springframework.http.HttpStatus;

public class NotFoundContentException extends ContentException {
    public NotFoundContentException(Long id) {
        super("Content not found:" + id, HttpStatus.NOT_FOUND);
    }
}
