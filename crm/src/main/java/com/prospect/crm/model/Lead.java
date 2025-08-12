package com.prospect.crm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leads")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leads_id_gen")
    @SequenceGenerator(name = "leads_id_gen", sequenceName = "leads_id_seq", initialValue = 1001, allocationSize = 1)
    @NotBlank
    private Long id;

    private String fullName;
    private String title;
    private String companyName;
    private String domain;
    private String linkedinUrl;
    private String source;
    private String status;
    private LocalDateTime createdAt;

    @ColumnDefault("true")
    private boolean isActive;

}