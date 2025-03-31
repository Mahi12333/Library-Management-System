package org.librarymanagementsystem.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@Table(name = "audit_logs")
@NoArgsConstructor
@AllArgsConstructor
public class DBAuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private String entityId;
    private String operation; // INSERT, UPDATE, DELETE

    @Column(columnDefinition = "TEXT")
    private String changes; // JSON-serialized field-level changes

    private String modifiedBy;

    @CreationTimestamp
    private Instant timestamp;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

    @PrePersist
    public void beforeInsert() {
        this.modifiedBy = getCurrentUser();
    }

    @PreUpdate
    public void beforeUpdate() {
        this.modifiedBy = getCurrentUser();
    }

    private String getCurrentUser() {
        if (SecurityContextHolder.getContext() == null || SecurityContextHolder.getContext().getAuthentication() == null) {
            return "SYSTEM"; // Default to "SYSTEM" or another placeholder
        }
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public void setChanges(String changesMap) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            this.changes = objectMapper.writeValueAsString(changesMap);
        } catch (Exception e) {
            throw new RuntimeException("Error serializing changes", e);
        }
    }
}
