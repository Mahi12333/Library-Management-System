package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.DBAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DBAuditLogRepository extends JpaRepository<DBAuditLog, Long> {
}
