package com.pard.root.exception.folder;


import org.springframework.http.HttpStatus;

public class NotFoundFolderException extends FolderException {
    public NotFoundFolderException(Long Id) {
        super("Folder Not Found: " + Id, HttpStatus.NOT_FOUND);
    }
}
