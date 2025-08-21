package com.prospect.crm.model;

import com.prospect.crm.constant.LogLevel;
import com.prospect.crm.constant.LogType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "system_logs", indexes = {
    @Index(name = "idx_system_logs_timestamp", columnList = "timestamp"),
    @Index(name = "idx_system_logs_level", columnList = "level"),
    @Index(name = "idx_system_logs_type", columnList = "type"),
    @Index(name = "idx_system_logs_user_id", columnList = "userId"),
    @Index(name = "idx_system_logs_ip_address", columnList = "ipAddress"),
    @Index(name = "idx_system_logs_level_timestamp", columnList = "level, timestamp"),
    @Index(name = "idx_system_logs_type_timestamp", columnList = "type, timestamp"),
    @Index(name = "idx_system_logs_user_timestamp", columnList = "userId, timestamp"),
    @Index(name = "idx_system_logs_http_status", columnList = "httpStatus"),
    @Index(name = "idx_system_logs_endpoint", columnList = "endpoint"),
    @Index(name = "idx_system_logs_execution_time", columnList = "executionTime")
})
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private LogLevel level;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private LogType type;

    @Column(length = 1000)
    private String message;
    
    @Column(length = 4000)
    private String details;
    
    @Column(columnDefinition = "TEXT")
    private String stackTrace;
    
    @Column(length = 255)
    private String className;
    
    @Column(length = 255)
    private String methodName;
    
    @Column(length = 50)
    private String userId;
    
    @Column(length = 45)
    private String ipAddress;
    
    @Column(length = 500)
    private String userAgent;
    
    private LocalDateTime timestamp;
    
    private Long executionTime;
    
    @Column(length = 100)
    private String requestId;
    
    @Column(length = 500)
    private String endpoint;
    
    @Column(length = 10)
    private String httpMethod;
    
    private Integer httpStatus;
    
    @Column(columnDefinition = "TEXT")
    private String requestBody;
    
    @Column(columnDefinition = "TEXT")
    private String responseBody;
} 