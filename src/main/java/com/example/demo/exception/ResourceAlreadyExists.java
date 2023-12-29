package com.example.demo.exception;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class ResourceAlreadyExists extends RuntimeException {
    public ResourceAlreadyExists(String message) {
        super(message);
    }
}
