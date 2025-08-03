package com.prospect.crm.controller;

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
@RequestMapping("/v1/Userr ")
public class UserController {
    private final UserService UserService;

    public UserController(UserService UserService) {
        this.UserService = UserService;
    }

    @GetMapping
    public List<UserListDto> getAllUserr () {
        return UserService.getAllUser();
    }

    @GetMapping("/{id}")
    public UserListDto getUserById(@PathVariable Long id) {
        return UserService.getUserById(id);
    }

    @PostMapping
    public ResponseEntity<UserListDto> createUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        return UserService.create(userRequestDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserListDto> updateUser(
                                                  @Valid @RequestBody UserRequestDto userRequestDto) {
        return UserService.update(userRequestDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        return UserService.delete(id);
    }
}
