package com.prospect.crm.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDto {
    @NotBlank(message = "{crm.constraint.name.notblank}")
    @Size(min = 3, max = 50, message = "{crm.constraint.name.size}")
    private String name;

    @NotBlank(message = "{crm.constraint.surname.notblank}")
    @Size(min = 3, max = 50, message = "{crm.constraint.surname.size}")
    private String surname;

    @NotBlank(message = "{crm.constraint.email.notblank}")
    @Email(message = "{crm.constraint.email.invalid}")
    private String email;

    private String phone;

    @NotBlank(message = "{crm.constraint.username.notblank}")
    @Size(min = 5, max = 50, message = "{crm.constraint.username.size}")
    private String username;

    @Size(min = 6, message = "{crm.constraint.password.size}")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d).*$", message = "Password must contain at least one letter and one number")
    private String password;

    private Long roleId;
    private Long subscriptionId;
}
