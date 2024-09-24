package com.app.blog.model.exception;

public class TagAlreadyExistsException extends RuntimeException {
    public TagAlreadyExistsException() {
        super("Tag with that name already exists");
    }
}
