package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.MethodAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MethodAuditLogRepository extends JpaRepository<MethodAuditLog, Long> {
}
