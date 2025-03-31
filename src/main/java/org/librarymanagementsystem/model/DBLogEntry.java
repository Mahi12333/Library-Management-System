package org.librarymanagementsystem.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.librarymanagementsystem.annotation.Auditable;

import java.time.LocalDateTime;


@Entity
@Table(name = "db_log_entry")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class DBLogEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "method", nullable = true)
    private String method;
    @Column(name = "principal", nullable = true)
    private String principal;
    @Column(name = "tableName", nullable = true)
    private String tableName;
    @Column(name = "action", nullable = true)
    private String action;
    @Column(name = "status", nullable = true)
    private Long status;
    @Column(name = "url", nullable = true)
    private String url;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @UpdateTimestamp
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;


    public DBLogEntry(String method, String principal, String tableName, String action) {
        this.method = method;
        this.principal = principal;
        this.tableName = tableName;
        this.action = action;
    }

    public DBLogEntry(String method, String principal, String url, Long status) {
        this.method = method;
        this.principal = principal;
        this.url = url;
        this.status = status;
    }
}
