package com.example.demo.exception;

public class UnauthorizedUserException extends RuntimeException {
    public UnauthorizedUserException(String message){
        super(message);
    }
}

