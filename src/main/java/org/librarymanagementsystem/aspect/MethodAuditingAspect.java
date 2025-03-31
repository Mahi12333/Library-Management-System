package org.librarymanagementsystem.aspect;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.librarymanagementsystem.model.MethodAuditLog;
import org.librarymanagementsystem.repository.MethodAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@Aspect
@Component
@Lazy
public class MethodAuditingAspect {
    @Autowired
    private MethodAuditLogRepository methodAuditLogRepository;

    @Autowired
    private AuditorAware<String> auditorAware;

    @Around("@annotation(Auditable)")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        log.info("MethodAuditingAspect: {}", joinPoint);
        // Get method name
        String methodName = joinPoint.getSignature().toShortString();

        // Get method arguments
        Object[] args = joinPoint.getArgs();
        String parameters = Arrays.toString(args);

        // Execute method and capture result
        Object result = joinPoint.proceed();

        // Execution time
        long executionTime = System.currentTimeMillis() - startTime;

        // Get current user
        String executedBy = auditorAware.getCurrentAuditor().orElse("UNKNOWN");

        // Save log
        MethodAuditLog log = MethodAuditLog.builder()
                .methodName(methodName)
                .parameters(parameters)
                .returnValue(Objects.toString(result, "NULL"))
                .executedBy(executedBy)
                .executionTime(executionTime)
                .build();

        methodAuditLogRepository.save(log);

        return result;
    }
}

