package com.prospect.crm.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends RuntimeException {
    
    private Map<String, String> fieldErrors;
    
    public ValidationException(String message) {
        super(message);
    }
} 