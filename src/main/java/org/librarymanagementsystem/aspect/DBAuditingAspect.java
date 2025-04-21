package org.librarymanagementsystem.aspect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Id;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.librarymanagementsystem.annotation.Auditable;
import org.librarymanagementsystem.model.DBAuditLog;
import org.librarymanagementsystem.repository.DBAuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
@Aspect
@Lazy
@Transactional
public class DBAuditingAspect {
    @Autowired
    private DBAuditLogRepository auditLogRepository;

    @Autowired
    private AuditorAware<String> auditorAware;

    @Autowired
    private ObjectMapper objectMapper;

    // Capture Save and Update (PUT, PATCH)
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.save(..))")
    public void savePointcut() {}

    // Capture Delete
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.delete*(..))")
    public void deletePointcut() {}

    // Capture Get (Read)
    @Pointcut("execution(* org.springframework.data.repository.CrudRepository+.find*(..))")
    public void getPointcut() {}

    // Handle Create and Update
    //@AfterReturning("savePointcut()")
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

            if (extractCreatedDate(entity) == null) {
                auditLog.setOperation("INSERT");
            } else {
                auditLog.setOperation("UPDATE");
            }

            auditLog.setCreatedDate(LocalDateTime.now());
            auditLog.setChanges(serializeChanges(changes));
            auditLog.setModifiedBy(modifiedBy);
            auditLogRepository.save(auditLog);
        }
    }

    // Handle Delete
    @Before("deletePointcut()")
    public void beforeDelete(JoinPoint joinPoint) {
        Object entity = joinPoint.getArgs()[0];

        if (entity instanceof Long) {
            // Fetch full entity before deleting
            Optional<?> optionalEntity = auditLogRepository.findById((Long) entity);
            if (optionalEntity.isPresent()) {
                entity = optionalEntity.get();
            } else {
                return;
            }
        }

        if (entity.getClass().isAnnotationPresent(Auditable.class)) {
            String entityName = entity.getClass().getSimpleName();
            String entityId = extractId(entity);
            String modifiedBy = auditorAware.getCurrentAuditor().orElse("UNKNOWN");

            DBAuditLog auditLog = new DBAuditLog();
            auditLog.setEntityName(entityName);
            auditLog.setEntityId(entityId);
            auditLog.setOperation("DELETE");
            auditLog.setModifiedBy(modifiedBy);
            auditLog.setCreatedDate(LocalDateTime.now());

            auditLogRepository.save(auditLog);
        }
    }

    // Handle GET (Read)
    @Before("getPointcut()")
    public void beforeGet(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();

        DBAuditLog auditLog = new DBAuditLog();
        auditLog.setEntityName(joinPoint.getSignature().getDeclaringType().descriptorString());
        auditLog.setOperation("READ");
        auditLog.setCreatedDate(LocalDateTime.now());
        auditLog.setModifiedBy(auditorAware.getCurrentAuditor().orElse("UNKNOWN"));
        auditLogRepository.save(auditLog);
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
