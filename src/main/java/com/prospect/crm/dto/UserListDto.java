package com.prospect.crm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserListDto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String username;
    private LocalDateTime createdAt;

}
