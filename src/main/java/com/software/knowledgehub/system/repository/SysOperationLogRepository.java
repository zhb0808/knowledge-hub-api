package com.software.knowledgehub.system.repository;

import com.software.knowledgehub.system.entity.SysOperationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SysOperationLogRepository extends
        JpaRepository<SysOperationLog, Long>,
        JpaSpecificationExecutor<SysOperationLog> {
}
