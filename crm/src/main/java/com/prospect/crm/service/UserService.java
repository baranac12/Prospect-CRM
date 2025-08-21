package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.dto.UserRequestDto;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.mapper.UserMapper;
import com.prospect.crm.model.Role;
import com.prospect.crm.model.SubscriptionType;
import com.prospect.crm.model.UserSubsInfo;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.RoleRepository;
import com.prospect.crm.repository.SubscriptionTypeRepository;
import com.prospect.crm.repository.UserRepository;
import com.prospect.crm.repository.UserSubsInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserSubsInfoRepository userSubsInfoRepository;
    private final SubscriptionTypeRepository subscriptionTypeRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, UserSubsInfoRepository userSubsInfoRepository,
                       SubscriptionTypeRepository subscriptionTypeRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userSubsInfoRepository = userSubsInfoRepository;
        this.subscriptionTypeRepository = subscriptionTypeRepository;
    }

    public List<Users> getAll() {
        return userRepository.findAll();
    }

    public Users getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + id));
    }

    public Users getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + email));
    }

    public Users getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.USER_NOT_FOUND + ": " + username));
    }

    public ResponseEntity<ApiResponse<UserListDto>> register(UserRequestDto userRequestDto) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new ValidationException(ErrorCode.USERNAME_ALREADY_EXISTS + ": " + userRequestDto.getUsername());
        }

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new ValidationException(ErrorCode.EMAIL_ALREADY_EXISTS + ": " + userRequestDto.getEmail());
        }

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new ResourceNotFoundException("Default USER role not found"));

        Users user = new Users();
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPhone(userRequestDto.getPhone());
        user.setUsername(userRequestDto.getUsername());
        user.setRoleId(defaultRole);
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);

        Users savedUser = userRepository.save(user);

        SubscriptionType trialType = subscriptionTypeRepository.findByName("Trial Plan")
                .orElseThrow(() -> new ResourceNotFoundException("Trial Plan subscription type not found"));

        UserSubsInfo trialSubscription = new UserSubsInfo();
        trialSubscription.setUsersId(savedUser);
        trialSubscription.setSubscriptionTypeId(trialType);
        trialSubscription.setSubsStartDate(LocalDateTime.now());
        trialSubscription.setSubsEndDate(LocalDateTime.now().plusDays(3));
        trialSubscription.setIsActive(true);
        trialSubscription.setCreatedAt(LocalDateTime.now());
        trialSubscription.setUpdatedAt(LocalDateTime.now());

        userSubsInfoRepository.save(trialSubscription);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(UserMapper.toUserList(savedUser), "User registered successfully"));
    }

    public ResponseEntity<ApiResponse<UserListDto>> update(Long id, UserRequestDto userRequestDto) {
        Users existingUser = getById(id);

        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent() &&
                !existingUser.getUsername().equals(userRequestDto.getUsername())) {
            throw new ValidationException(ErrorCode.USERNAME_ALREADY_EXISTS + ": " + userRequestDto.getUsername());
        }

        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent() &&
                !existingUser.getEmail().equals(userRequestDto.getEmail())) {
            throw new ValidationException(ErrorCode.EMAIL_ALREADY_EXISTS + ": " + userRequestDto.getEmail());
        }

        existingUser.setName(userRequestDto.getName());
        existingUser.setSurname(userRequestDto.getSurname());
        existingUser.setEmail(userRequestDto.getEmail());
        existingUser.setPhone(userRequestDto.getPhone());
        existingUser.setUsername(userRequestDto.getUsername());
        existingUser.setUpdatedAt(LocalDateTime.now());

        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        Users updatedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(ApiResponse.success(UserMapper.toUserList(updatedUser), "User updated successfully"));
    }

    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        Users user = getById(id);
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }

    public ResponseEntity<ApiResponse<Void>> activate(Long id) {
        Users user = getById(id);
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(null, "User activated successfully"));
    }

    public ResponseEntity<ApiResponse<Void>> deactivate(Long id) {
        Users user = getById(id);
        user.setIsActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return ResponseEntity.ok(ApiResponse.success(null, "User deactivated successfully"));
    }

    public ResponseEntity<ApiResponse<Void>> changePassword(Long id, String oldPassword, String newPassword) {
        Users user = getById(id);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ValidationException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    private String generateResetToken() {
        return java.util.UUID.randomUUID().toString();
    }
}