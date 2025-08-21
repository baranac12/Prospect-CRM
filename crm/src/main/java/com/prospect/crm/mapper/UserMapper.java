package com.prospect.crm.mapper;

import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.model.Users;

public class UserMapper {

    public static UserListDto toUserList(Users entity) {
        return UserListDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .username(entity.getUsername())
                .roleId(entity.getRoleId() != null ? entity.getRoleId().getId() : null)
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
