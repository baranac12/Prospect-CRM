package com.prospect.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

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

    @NotBlank(message = "{crm.constraint.password.notblank}")
    @Size(min = 8, message = "{crm.constraint.password.size}")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role roleId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ColumnDefault("true")
    private Boolean isActive = true;
}
