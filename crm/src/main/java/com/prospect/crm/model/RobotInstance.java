package com.prospect.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "robot_instances")
public class RobotInstance {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "robot_instance_seq")
    @SequenceGenerator(name = "robot_instance_seq", sequenceName = "robot_instances_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "robot_type", length = Integer.MAX_VALUE)
    private String robotType;

    @Column(name = "status", length = Integer.MAX_VALUE)
    private String status;

    @Column(name = "launched_by", length = Integer.MAX_VALUE)
    private String launchedBy;

    @Column(name = "launch_time")
    private LocalDateTime launchTime;

    @Column(name = "completed_time")
    private LocalDateTime completedTime;

}