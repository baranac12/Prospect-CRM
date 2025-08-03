package com.prospect.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")  // Tablo adı küçük ve tekil/çoğul tutarlı olsun
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq")
    @SequenceGenerator(name = "users_id_seq", sequenceName = "users_id_seq", allocationSize = 1)
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

    @NotBlank(message = "{crm.constraint.password.notblank}")
    @Size(min = 8, message = "{crm.constraint.password.size}")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscriptionId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ColumnDefault("true")
    private Boolean isActive = true;
}
