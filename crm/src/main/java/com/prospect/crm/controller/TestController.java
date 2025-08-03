package com.prospect.crm.controller;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.PaginationInfo;
import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/test")
public class TestController {

    @GetMapping("/success")
    public ResponseEntity<ApiResponse<String>> testSuccess() {
        return ResponseEntity.ok(ApiResponse.success("Test successful", "Operation completed successfully"));
    }

    @GetMapping("/data")
    public ResponseEntity<ApiResponse<List<UserListDto>>> testDataResponse() {
        List<UserListDto> users = Arrays.asList(
                UserListDto.builder()
                        .id(1001L)
                        .name("John")
                        .surname("Doe")
                        .email("john.doe@example.com")
                        .phone("5551234567")
                        .username("johndoe")
                        .build(),
                UserListDto.builder()
                        .id(1002L)
                        .name("Jane")
                        .surname("Smith")
                        .email("jane.smith@example.com")
                        .phone("5559876543")
                        .username("janesmith")
                        .build()
        );
        
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/pagination")
    public ResponseEntity<ApiResponse<List<UserListDto>>> testPaginationResponse() {
        List<UserListDto> users = Arrays.asList(
                UserListDto.builder()
                        .id(1001L)
                        .name("John")
                        .surname("Doe")
                        .email("john.doe@example.com")
                        .phone("5551234567")
                        .username("johndoe")
                        .build()
        );
        
        PaginationInfo pagination = PaginationInfo.of(0, 10, 25);
        
        return ResponseEntity.ok(ApiResponse.successWithPagination(users, pagination));
    }

    @GetMapping("/error")
    public ResponseEntity<ApiResponse<Void>> testErrorResponse() {
        // This will be handled by GlobalExceptionHandler
        throw new RuntimeException("Test error occurred");
    }

    @GetMapping("/validation-error")
    public ResponseEntity<ApiResponse<Void>> testValidationError() {
        // This will demonstrate ERR_ format error codes
        throw new ValidationException(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());
    }

    @GetMapping("/custom-error")
    public ResponseEntity<ApiResponse<Void>> testCustomError() {
        return ResponseEntity.badRequest()
                .body(ApiResponse.error(
                        "Custom error message",
                        ErrorCode.USER_NOT_FOUND.getCode(),
                        "This is a custom error with ERR_ format"
                ));
    }
} 