package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    
    private String code;
    private String message;
    private String details;
    private String field;
    private Object value;
    
    public static ErrorResponse of(String code, String message) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .build();
    }
    
    public static ErrorResponse of(String code, String message, String details) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();
    }
    
    public static ErrorResponse of(String code, String message, String field, Object value) {
        return ErrorResponse.builder()
                .code(code)
                .message(message)
                .field(field)
                .value(value)
                .build();
    }
} 