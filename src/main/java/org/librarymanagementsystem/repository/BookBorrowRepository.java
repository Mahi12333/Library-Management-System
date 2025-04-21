package org.librarymanagementsystem.repository;


import org.librarymanagementsystem.model.Borrowed_book_records;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookBorrowRepository extends JpaRepository<Borrowed_book_records, Long> {

    @Query("SELECT FUNCTION('DATE', br.borrowedAt) as date, COUNT(*) as count " + "From Borrowed_book_records br " + "WHERE br.borrowedAt BETWEEN :startDate AND :endDate " + "GROUP BY FUNCTION('DATE', br.borrowedAt)")
    Map<String, Long> getBorrowingTrendsBetweenDates(LocalDate startDate, LocalDate endDate);

    @Query(value = "Select b.title as bookTitle, COUNT(*) as BorrowBookCount,  " + "From borrowed_books br JOIN books b ON br.book_id = b.id " + "GROUP BY b.id ORDER BY BorrowBookCount DESC LIMIT :limit", nativeQuery = true)
    List<Map<String,Object>> findMostBorrowedBooks(int limit);

    @Query("SELECT b FROM Borrowed_book_records b JOIN FETCH b.book JOIN FETCH b.user WHERE b.id = :id")
    Optional<Borrowed_book_records> findByIdWithBookAndUser(@Param("id") Long id);

}
