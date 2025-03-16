package com.pard.root.exception.content;

import org.springframework.http.HttpStatus;

public class NotAccessedContentException extends ContentException {
    public NotAccessedContentException(Long id) {
        super("You do not own this Content.: " + id, HttpStatus.UNAUTHORIZED);
    }
}
