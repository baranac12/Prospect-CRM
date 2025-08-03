package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.dto.UserRequestDto;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.mapper.UserMapper;
import com.prospect.crm.model.Users;
import com.prospect.crm.repository.RoleRepository;
import com.prospect.crm.repository.SubscriptionRepository;
import com.prospect.crm.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, RoleRepository roleRepository, SubscriptionRepository subscriptionRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return User.builder()
                .username(users.getUsername())
                .password(users.getPassword())
                .authorities(users.getRoleId().getName())
                .build();
    }

    public List<UserListDto> getAllUser() {
        return userRepository.findAll().stream()
                .filter(Users::getIsActive)
                .map(UserMapper::toUserList)
                .collect(toList());
    }

    public UserListDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::toUserList)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public ResponseEntity<UserListDto> create(UserRequestDto userRequestDto) {
        // Check if username already exists
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new ValidationException(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(userRequestDto.getEmail()).isPresent()) {
            throw new ValidationException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
        }

        Users user = new Users();
        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPhone(userRequestDto.getPhone());
        user.setUsername(userRequestDto.getUsername());
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        
        // Set role
        user.setRoleId(roleRepository.findById(userRequestDto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userRequestDto.getRoleId())));
        
        // Set subscription
        user.setSubscriptionId(subscriptionRepository.findById(userRequestDto.getSubscriptionId())
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + userRequestDto.getSubscriptionId())));
        
        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.toUserList(user));
    }

    public ResponseEntity<UserListDto> update(UserRequestDto userRequestDto) {
        Users user = userRepository.findById(userRequestDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userRequestDto.getId()));

        // Check username uniqueness (excluding current user)
        userRepository.findByUsername(userRequestDto.getUsername())
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(u -> {
                    throw new ValidationException(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());
                });

        // Check email uniqueness (excluding current user)
        userRepository.findByEmail(userRequestDto.getEmail())
                .filter(u -> !u.getId().equals(user.getId()))
                .ifPresent(u -> {
                    throw new ValidationException(ErrorCode.EMAIL_ALREADY_EXISTS.getMessage());
                });

        user.setName(userRequestDto.getName());
        user.setSurname(userRequestDto.getSurname());
        user.setEmail(userRequestDto.getEmail());
        user.setPhone(userRequestDto.getPhone());
        user.setUsername(userRequestDto.getUsername());
        user.setIsActive(true);
        user.setUpdatedAt(LocalDateTime.now());

        // Update password only if provided
        if (userRequestDto.getPassword() != null && !userRequestDto.getPassword().isEmpty() && !userRequestDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        }

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toUserList(user));
    }

    public ResponseEntity<String> delete(Long id) {
        Users user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setIsActive(false);
        userRepository.save(user);
        return ResponseEntity.noContent().build();
    }
}