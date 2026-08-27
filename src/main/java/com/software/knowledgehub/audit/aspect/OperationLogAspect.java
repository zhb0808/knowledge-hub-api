package com.software.knowledgehub.audit.aspect;

import com.software.knowledgehub.audit.annotation.OperationLog;
import com.software.knowledgehub.security.model.AuthenticatedUser;
import com.software.knowledgehub.system.entity.SysOperationLog;
import com.software.knowledgehub.system.service.OperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.OffsetDateTime;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;

    @Around("@annotation(operationLog)")
    public Object recordOperation(
            ProceedingJoinPoint joinPoint,
            OperationLog operationLog) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            saveOperationLog(operationLog, true, null);
            return result;
        } catch (Throwable exception) {
            saveOperationLog(operationLog, false, exception);
            throw exception;
        }
    }

    private void saveOperationLog(
            OperationLog operation,
            boolean success,
            Throwable failure) {
        try {
            // 读取当前请求和已经通过认证的用户。
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            AuthenticatedUser user = (AuthenticatedUser) SecurityContextHolder.getContext()
                    .getAuthentication()
                    .getPrincipal();

            SysOperationLog operationLog = new SysOperationLog();
            operationLog.setOperatorId(user.getId());
            operationLog.setOperatorName(user.getUsername());
            operationLog.setModule(operation.module());
            operationLog.setAction(operation.action());
            operationLog.setRequestMethod(request.getMethod());
            operationLog.setRequestPath(request.getRequestURI());
            operationLog.setTraceId(MDC.get("traceId"));
            operationLog.setSuccess(success);
            operationLog.setCreatedTime(OffsetDateTime.now());

            if (failure != null) {
                String errorMessage = failure.getMessage();
                if (errorMessage != null && errorMessage.length() > 500) {
                    errorMessage = errorMessage.substring(0, 500);
                }
                operationLog.setErrorMessage(errorMessage);
            }

            // 使用独立事务保存操作结果。
            operationLogService.saveOperationLog(operationLog);
        } catch (Exception exception) {
            // 审计保存失败不能覆盖原业务结果。
            log.error("操作日志保存失败，traceId={}", MDC.get("traceId"), exception);
        }
    }
}
