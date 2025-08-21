package com.prospect.crm.model;

import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "leads")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leads_id_gen")
    @SequenceGenerator(name = "leads_id_gen", sequenceName = "leads_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private Users usersId;

    private String fullName;
    private String title;
    private String companyName;
    private String domain;
    private String linkedinUrl;
    private String source;
    private String status;
    private LocalDateTime createdAt;

    @ColumnDefault("true")
    private Boolean isActive = true;
}