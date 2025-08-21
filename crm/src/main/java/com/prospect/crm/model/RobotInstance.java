package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "robot_instances")
public class RobotInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "robot_instance_seq")
    @SequenceGenerator(name = "robot_instance_seq", sequenceName = "robot_instances_id_seq", initialValue = 1001, allocationSize = 1)
    private Long id;

    private String robotType;
    private String status;
    private String launchedBy;
    private LocalDateTime launchTime;
    private LocalDateTime completedTime;
}