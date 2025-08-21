package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lead_email_guesses")
public class LeadEmailGuess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lead_email_guesses_id_gen")
    @SequenceGenerator(name = "lead_email_guesses_id_gen", sequenceName = "lead_email_guesses_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private Lead leadId;

    private String guessedEmail;
    private Double confidenceScore;
    private Boolean validated;
    private LocalDateTime createdAt;
}