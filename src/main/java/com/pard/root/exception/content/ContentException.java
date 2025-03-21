package com.pard.root.exception.content;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ContentException extends RuntimeException {
    private final ContentExceptionCode contentExceptionCode;
    public ContentException(ContentExceptionCode contentExceptionCode) {
        super(contentExceptionCode.getMessage());
        this.contentExceptionCode = contentExceptionCode;
    }
}
