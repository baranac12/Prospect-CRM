package com.prospect.crm.controller;

import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.dto.UserRequestDto;
import com.prospect.crm.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public List<UserListDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserListDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserListDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.create(userRequestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserListDto> updateUser(
                                                  @Valid @RequestBody UserRequestDto userRequestDto) {
        return userService.update(userRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return userService.delete(id);
    }
}
