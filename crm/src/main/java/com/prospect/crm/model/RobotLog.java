package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "robot_logs")
public class RobotLog {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "robot_log_seq")
    @SequenceGenerator(name = "robot_log_seq", sequenceName = "robot_logs_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robot_id")
    private RobotInstance robotId;

    private String action;
    private String result;
    private LocalDateTime timestamp;
    private String logDetails;
}