package org.librarymanagementsystem.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Id;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.librarymanagementsystem.annotation.Auditable;
import org.librarymanagementsystem.model.DBAuditLog;
import org.librarymanagementsystem.repository.DBAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Aspect
@Lazy
public class DBAuditingAspect {
    @Autowired
    private DBAuditLogRepository auditLogRepository;

    @Autowired
    private AuditorAware<String> auditorAware;

    @Autowired
    private ObjectMapper objectMapper;

    @Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.save(..))")
    public void savePointcut() {}

    @Pointcut("execution(* org.springframework.data.jpa.repository.JpaRepository+.delete(..))")
    public void deletePointcut() {}

    @Before("savePointcut()")
    public void beforeSave(JoinPoint joinPoint) {
        Object entity = joinPoint.getArgs()[0];

        if (entity.getClass().isAnnotationPresent(Auditable.class)) {
            String entityName = entity.getClass().getSimpleName();
            String entityId = extractId(entity);
            Map<String, Object> changes = extractFields(entity);
            String modifiedBy = auditorAware.getCurrentAuditor().orElse("UNKNOWN");

            DBAuditLog auditLog = new DBAuditLog();
            auditLog.setEntityName(entityName);
            auditLog.setEntityId(entityId);

            // Detect INSERT or UPDATE
            if (extractCreatedDate(entity) == null) {
                auditLog.setOperation("INSERT");
            } else {
                auditLog.setOperation("UPDATE");
            }

            auditLog.setChanges(serializeChanges(changes));
            auditLog.setModifiedBy(modifiedBy);
            auditLogRepository.save(auditLog);
        }
    }

    @Before("deletePointcut()")
    public void beforeDelete(JoinPoint joinPoint) {
        Object entity = joinPoint.getArgs()[0];

        if (entity.getClass().isAnnotationPresent(Auditable.class)) {
            String entityName = entity.getClass().getSimpleName();
            String entityId = extractId(entity);
            String modifiedBy = auditorAware.getCurrentAuditor().orElse("UNKNOWN");

            DBAuditLog auditLog = new DBAuditLog();
            auditLog.setEntityName(entityName);
            auditLog.setEntityId(entityId);
            auditLog.setOperation("DELETE");
            auditLog.setModifiedBy(modifiedBy);

            auditLogRepository.save(auditLog);
        }
    }

    private String extractId(Object entity) {
        try {
            for (Field field : entity.getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    field.setAccessible(true);
                    return Optional.ofNullable(field.get(entity)).map(Object::toString).orElse("UNKNOWN");
                }
            }
        } catch (Exception ignored) {}
        return "UNKNOWN";
    }

    private LocalDateTime extractCreatedDate(Object entity) {
        try {
            Field createdDateField = ReflectionUtils.findField(entity.getClass(), "createdDate");
            if (createdDateField != null) {
                createdDateField.setAccessible(true);
                return (LocalDateTime) createdDateField.get(entity);
            }
        } catch (Exception ignored) {}
        return null;
    }

    private Map<String, Object> extractFields(Object entity) {
        Map<String, Object> changes = new HashMap<>();
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                changes.put(field.getName(), field.get(entity));
            } catch (IllegalAccessException ignored) {}
        }
        return changes;
    }

    private String serializeChanges(Map<String, Object> changes) {
        try {
            return objectMapper.writeValueAsString(changes);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
