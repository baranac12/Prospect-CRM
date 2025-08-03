package com.prospect.crm.exception;

public class RobotException extends RuntimeException {
    
    public RobotException(String message) {
        super(message);
    }
    
    public RobotException(String message, Throwable cause) {
        super(message, cause);
    }
} 