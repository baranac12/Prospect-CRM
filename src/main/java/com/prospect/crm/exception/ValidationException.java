package com.prospect.crm.exception;

import java.util.List;
import java.util.Map;

public class ValidationException extends RuntimeException {
    
    private Map<String, List<String>> fieldErrors;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Map<String, List<String>> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public Map<String, List<String>> getFieldErrors() {
        return fieldErrors;
    }
} 