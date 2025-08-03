package com.prospect.crm.mapper;

import com.prospect.crm.dto.UserListDto;
import com.prospect.crm.model.Users;

public class UserMapper {

    public static UserListDto toUserList(Users entity) {
        return new UserListDto(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getUsername(),
                entity.getCreatedAt()
        );


    }
}
