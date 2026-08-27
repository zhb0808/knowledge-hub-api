package com.software.knowledgehub.system.service;

import com.software.knowledgehub.system.dto.OperationLogQueryDTO;
import com.software.knowledgehub.system.entity.SysOperationLog;
import com.software.knowledgehub.system.vo.OperationLogVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OperationLogService {

    /**
     * 保存一条操作日志。
     */
    void saveOperationLog(SysOperationLog operationLog);

    /**
     * 分页查询操作日志。
     */
    Page<OperationLogVO> listOperationLogs(OperationLogQueryDTO request, Pageable pageable);
}
