package com.prospect.crm.controller;

import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.dto.UserRequestDto;
import com.prospect.crm.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserListDto>>> getAllUsers() {
        List<UserListDto> users = userService.getAllUser();
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserListDto>> getUserById(@PathVariable Long id) {
        UserListDto user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserListDto>> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        ResponseEntity<UserListDto> response = userService.create(userRequestDto);
        UserListDto user = response.getBody();
        return ResponseEntity.status(response.getStatusCode())
                .body(ApiResponse.success(user, "User created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserListDto>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDto userRequestDto) {
        userRequestDto.setId(id);
        ResponseEntity<UserListDto> response = userService.update(userRequestDto);
        UserListDto user = response.getBody();
        return ResponseEntity.status(response.getStatusCode())
                .body(ApiResponse.success(user, "User updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        ResponseEntity<String> response = userService.delete(id);
        return ResponseEntity.status(response.getStatusCode())
                .body(ApiResponse.success("User deleted successfully"));
    }
}
