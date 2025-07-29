package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "lead_email_guesses")
public class LeadEmailGuess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lead_email_guesses_id_gen")
    @SequenceGenerator(name = "lead_email_guesses_id_gen", sequenceName = "lead_email_guesses_id_seq", initialValue = 1001, allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead leadId;

    @Column(name = "guessed_email", length = Integer.MAX_VALUE)
    private String guessedEmail;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "validated")
    private Boolean validated;

    @Column(name = "created_at")
    private Instant createdAt;

}