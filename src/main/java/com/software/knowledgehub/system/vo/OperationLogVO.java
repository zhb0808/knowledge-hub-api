package com.software.knowledgehub.system.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;

@Getter
@AllArgsConstructor
public class OperationLogVO {

    private Long id;
    private Long operatorId;
    private String operatorName;
    private String module;
    private String action;
    private String requestMethod;
    private String requestPath;
    private String traceId;
    private Boolean success;
    private String errorMessage;
    private OffsetDateTime createdTime;
}
