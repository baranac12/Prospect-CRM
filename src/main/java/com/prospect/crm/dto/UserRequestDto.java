package com.prospect.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    private Long id;

    @NotBlank(message = "{crm.constraint.name.notblank}")
    @Size(min = 3, max = 50, message = "{crm.constraint.name.size}")
    private String name;

    @NotBlank(message = "{crm.constraint.surname.notblank}")
    @Size(min = 3, max = 50, message = "{crm.constraint.surname.size}")
    private String surname;

    @NotBlank(message = "{crm.constraint.email.notblank}")
    @Email(message = "{crm.constraint.email.rules}")
    private String email;

    @NotBlank(message = "{crm.constraint.phone.notblank}")
    @Size(min = 11, max = 11, message = "{crm.constraint.phone.size}")
    private String phone;

    @NotBlank(message = "{crm.constraint.username.notblank}")
    @Size(min = 5, max = 50, message = "{crm.constraint.username.size}")
    private String username;

    @Size(min = 8, message = "{crm.constraint.password.size}")
    private String password;

    private Long roleId;
    private Long subscriptionId;
}
