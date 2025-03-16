package com.pard.root.exception.folder;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FolderException extends RuntimeException {
    private final HttpStatus status;

    public FolderException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
