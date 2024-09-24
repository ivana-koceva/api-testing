package com.app.blog.model.exception.handler;

import com.app.blog.model.exception.BlogPostNotFoundException;
import com.app.blog.model.exception.TagAlreadyExistsException;
import com.app.blog.model.exception.TagNotFoundException;
import com.app.blog.model.exception.response.ExceptionResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BlogPostNotFoundException.class)
    public ResponseEntity<Object> handleBlogPostNotFoundException(BlogPostNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(
                response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagNotFoundException.class)
    public ResponseEntity<Object> handleTagNotFoundException(TagNotFoundException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(
                response, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TagAlreadyExistsException.class)
    public ResponseEntity<Object> handleTagAlreadyExistsException(TagAlreadyExistsException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(
                response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {

        List<ExceptionResponse> exceptionResponses = new ArrayList<>();
        BindingResult bindingResult = ex.getBindingResult();
        List<ObjectError> errors = bindingResult.getAllErrors();
        for(ObjectError error : errors) {
            exceptionResponses.add(new ExceptionResponse(error.getDefaultMessage(), HttpStatus.BAD_REQUEST));
        }

        return new ResponseEntity<>(exceptionResponses, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Object> handleHttpClientErrorException(HttpClientErrorException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage().substring(19, ex.getMessage().indexOf(",")-1), HttpStatus.BAD_REQUEST);
        if(response.getMessage().startsWith("\""))
            response.setMessage(response.getMessage().substring(1));
        return new ResponseEntity<>(response, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}

