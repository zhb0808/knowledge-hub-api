package com.software.knowledgehub.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "sys_operation_log")
public class SysOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "operator_id")
    private Long operatorId;

    @Column(name = "operator_name", length = 50)
    private String operatorName;

    @Column(nullable = false, length = 50)
    private String module;

    @Column(nullable = false, length = 100)
    private String action;

    @Column(name = "request_method", nullable = false, length = 10)
    private String requestMethod;

    @Column(name = "request_path", nullable = false, length = 255)
    private String requestPath;

    @Column(name = "trace_id", nullable = false, length = 32)
    private String traceId;

    @Column(nullable = false)
    private Boolean success;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    @Column(name = "created_time", nullable = false)
    private OffsetDateTime createdTime;
}
