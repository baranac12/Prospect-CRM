package com.prospect.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "robot_instances")
public class RobotInstance {
    @Id
    @ColumnDefault("nextval('robot_instances_id_seq')")
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