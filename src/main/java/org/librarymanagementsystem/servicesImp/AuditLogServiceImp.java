package org.librarymanagementsystem.servicesImp;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.librarymanagementsystem.model.DBLogEntry;
import org.librarymanagementsystem.repository.DbLogRepository;
import org.librarymanagementsystem.services.AuditLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImp implements AuditLogService {
      private final DbLogRepository dbLogEntryRepository;

    @Transactional
    @Override
    public CompletableFuture<Void> createLogAsync(String method, String principal, String tableName, String action) {
        log.info("createLogAsync----{}",method);
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
        //dbLogEntryRepository.deleteLogsOlderThan(twoDaysAgo); // Delete old logs
        DBLogEntry logEntry = new DBLogEntry(method, principal, tableName, action);
        dbLogEntryRepository.save(logEntry);
        log.info("Logged Action: {}", logEntry);
        return CompletableFuture.completedFuture(null);

    }

    @Transactional
    @Override
    public CompletableFuture<Void> createLogAsync(String method, String principal, String url, int status) {
        try {
            log.info("createLogAsyncs----{}",method);
            LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(2);
            //dbLogEntryRepository.deleteLogsOlderThan(twoDaysAgo); // Delete old logs
            DBLogEntry logEntry = new DBLogEntry(method, principal, url, Long.valueOf(status));
            dbLogEntryRepository.save(logEntry);
            log.info("Logged Response: {}", logEntry);
        } catch (Exception e) {
            log.error("Failed to log response: {}", e.getMessage());
        }
        return CompletableFuture.completedFuture(null);
    }
}
