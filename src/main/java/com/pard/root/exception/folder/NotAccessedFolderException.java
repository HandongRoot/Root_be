package com.pard.root.exception.folder;

import org.springframework.http.HttpStatus;

public class NotAccessedFolderException extends FolderException {
    public NotAccessedFolderException(Long id) {
        super("You do not own this folder.: " + id,  HttpStatus.FORBIDDEN);
    }
}
