package com.prospect.crm.service;

import com.prospect.crm.constant.ErrorCode;
import com.prospect.crm.dto.ApiResponse;
import com.prospect.crm.exception.ResourceNotFoundException;
import com.prospect.crm.exception.ValidationException;
import com.prospect.crm.model.Role;
import com.prospect.crm.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<Role> getAll() {
        return roleRepository.findAll().stream()
                .toList();
    }

    public Role getById(Long id) {
        return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCode.ROLE_NOT_FOUND + " : " + id));
    }

    public ResponseEntity<ApiResponse<Role>> create(Role role) {
        if (roleRepository.findByName(role.getName()).isPresent()) {
            throw new ValidationException(ErrorCode.ROLE_ALREADY_EXISTS + " : " + role.getName());
        }
        roleRepository.save(role);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(role,"Role created."));
    }

    public ResponseEntity<ApiResponse<Role>> update(Long id, Role role) {
        Role roles = getById(id);
        roles.setName(role.getName());
        roleRepository.save(roles);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(role,"Role updated."));
    }
    public ResponseEntity<ApiResponse<Void>> delete(Long id) {
        Role roles = getById(id);
        roles.setIsActive(false);
        roleRepository.save(roles);
        return ResponseEntity.ok(ApiResponse.success(null, "Role Deleted"));
    }


}
