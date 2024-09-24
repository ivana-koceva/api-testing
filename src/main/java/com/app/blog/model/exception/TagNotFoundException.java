package com.app.blog.model.exception;

public class TagNotFoundException extends RuntimeException{
    public TagNotFoundException() {
        super("Tag not found");
    }
}
