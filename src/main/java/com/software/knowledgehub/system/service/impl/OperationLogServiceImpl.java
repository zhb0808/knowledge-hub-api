package com.software.knowledgehub.system.service.impl;

import com.software.knowledgehub.system.dto.OperationLogQueryDTO;
import com.software.knowledgehub.system.entity.SysOperationLog;
import com.software.knowledgehub.system.repository.SysOperationLogRepository;
import com.software.knowledgehub.system.service.OperationLogService;
import com.software.knowledgehub.system.vo.OperationLogVO;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OperationLogServiceImpl implements OperationLogService {

    private final SysOperationLogRepository operationLogRepository;

    /**
     * 保存一条操作日志。
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveOperationLog(SysOperationLog operationLog) {
        operationLogRepository.save(operationLog);
    }

    /**
     * 分页查询操作日志。
     */
    @Override
    public Page<OperationLogVO> listOperationLogs(
            OperationLogQueryDTO request,
            Pageable pageable) {
        Specification<SysOperationLog> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getOperatorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("operatorId"), request.getOperatorId()));
            }
            if (request.getModule() != null) {
                predicates.add(criteriaBuilder.equal(root.get("module"), request.getModule()));
            }
            if (request.getAction() != null) {
                predicates.add(criteriaBuilder.equal(root.get("action"), request.getAction()));
            }
            if (request.getSuccess() != null) {
                predicates.add(criteriaBuilder.equal(root.get("success"), request.getSuccess()));
            }
            if (request.getStartDate() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdTime"),
                        request.getStartDate().atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()));
            }
            if (request.getEndDate() != null) {
                predicates.add(criteriaBuilder.lessThan(
                        root.get("createdTime"),
                        request.getEndDate().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()));
            }
            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };

        // 按查询条件分页加载操作日志。
        return operationLogRepository.findAll(specification, pageable).map(operationLog ->
                new OperationLogVO(
                        operationLog.getId(),
                        operationLog.getOperatorId(),
                        operationLog.getOperatorName(),
                        operationLog.getModule(),
                        operationLog.getAction(),
                        operationLog.getRequestMethod(),
                        operationLog.getRequestPath(),
                        operationLog.getTraceId(),
                        operationLog.getSuccess(),
                        operationLog.getErrorMessage(),
                        operationLog.getCreatedTime()
                ));
    }
}
