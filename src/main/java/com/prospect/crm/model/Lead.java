package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "leads")
public class Lead {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leads_id_gen")
    @SequenceGenerator(name = "leads_id_gen", sequenceName = "leads_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "full_name", length = Integer.MAX_VALUE)
    private String fullName;

    @Column(name = "title", length = Integer.MAX_VALUE)
    private String title;

    @Column(name = "company_name", length = Integer.MAX_VALUE)
    private String companyName;

    @Column(name = "domain", length = Integer.MAX_VALUE)
    private String domain;

    @Column(name = "linkedin_url", length = Integer.MAX_VALUE)
    private String linkedinUrl;

    @Column(name = "source", length = Integer.MAX_VALUE)
    private String source;

    @Column(name = "status", length = Integer.MAX_VALUE)
    private String status;

    @Column(name = "created_at")
    private Instant createdAt;

}