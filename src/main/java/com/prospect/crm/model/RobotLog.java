package com.prospect.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "robot_logs")
public class RobotLog {
    @Id
    @ColumnDefault("nextval('robot_logs_id_seq')")
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "robot_id")
    private RobotInstance robotId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User userId;

    @Column(name = "action", length = Integer.MAX_VALUE)
    private String action;

    @Column(name = "result", length = Integer.MAX_VALUE)
    private String result;

    @Column(name = "timestamp")
    private Instant timestamp;

    @Column(name = "log_details", length = Integer.MAX_VALUE)
    private String logDetails;

}