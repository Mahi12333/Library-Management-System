package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.Borrowed_book_records;
import org.librarymanagementsystem.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT b FROM Borrowed_book_records b WHERE b.dueDate BETWEEN :today AND :twoDaysFromNow")
    List<Borrowed_book_records> findBorrowingsDueInDays(@Param("today") Date today, @Param("twoDaysFromNow") Date twoDaysFromNow);
}
