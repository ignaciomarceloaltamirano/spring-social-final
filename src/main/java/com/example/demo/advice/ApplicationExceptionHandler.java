package com.example.demo.advice;

import com.example.demo.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApplicationExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedUserException.class)
    public Map<String,String> handleUnauthorizedUserException(UnauthorizedUserException ex, WebRequest request){
        Map<String,String> errorMap=new HashMap<>();
        errorMap.put("message",ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Map<String,String> handleResourceNotFoundException(ResourceNotFoundException ex,WebRequest request){
        Map<String,String> errorMap=new HashMap<>();
        errorMap.put("message",ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UserAlreadyExistsException.class)
    public Map<String,String> handleUserAlreadyExistsException(UserAlreadyExistsException ex,WebRequest request){
        Map<String,String> errorMap=new HashMap<>();
        errorMap.put("message",ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ResourceAlreadyExists.class)
    public Map<String,String> handleResourceAlreadyExists(ResourceAlreadyExists ex,WebRequest request){
        Map<String,String> errorMap=new HashMap<>();
        errorMap.put("message",ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(TokenRefreshException.class)
    public Map<String, String> handleTokenRefreshException(TokenRefreshException ex, WebRequest request) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleInvalidArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message",ex.getMessage());
        return errorMap;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Map<String,String> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex){
        Map<String,String> errorMap=new HashMap<>();
        errorMap.put("message",ex.getMessage());
        return errorMap;
    }
}

