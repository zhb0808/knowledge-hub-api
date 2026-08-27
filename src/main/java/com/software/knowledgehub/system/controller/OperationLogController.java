package com.software.knowledgehub.system.controller;

import com.software.knowledgehub.common.response.ApiResponse;
import com.software.knowledgehub.system.dto.OperationLogQueryDTO;
import com.software.knowledgehub.system.service.OperationLogService;
import com.software.knowledgehub.system.vo.OperationLogVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    public ApiResponse<Page<OperationLogVO>> listOperationLogs(
            OperationLogQueryDTO request,
            @SortDefault(
                    sort = "createdTime",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return ApiResponse.success(operationLogService.listOperationLogs(request, pageable));
    }
}
