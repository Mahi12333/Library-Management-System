package org.librarymanagementsystem.services;


import java.util.concurrent.CompletableFuture;

public interface AuditLogService {
    CompletableFuture<Void> createLogAsync(String method, String principal, String tableName, String action);
    CompletableFuture<Void> createLogAsync(String method, String principal, String url, int status);
}
