package com.app.blog.model.exception;

public class BlogPostNotFoundException extends RuntimeException {
    public BlogPostNotFoundException() {
        super("Blog post not found");
    }
}
