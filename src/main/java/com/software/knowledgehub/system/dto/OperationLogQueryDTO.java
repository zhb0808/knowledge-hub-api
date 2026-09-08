package com.software.knowledgehub.system.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class OperationLogQueryDTO {

    @Size(max = 50, message = "操作人账号长度不能超过50个字符")
    private String operatorName;

    private String module;

    private String action;

    private Boolean success;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}
