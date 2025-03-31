package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.DBLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface DbLogRepository extends JpaRepository<DBLogEntry, Long> {

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM DBLogEntry l WHERE l.created_date < :cutoffDate")
//    void deleteLogsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);

}
