package org.librarymanagementsystem.repository;


import org.librarymanagementsystem.model.Borrowed_book_records;
import org.librarymanagementsystem.payload.response.BorrowBooksDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BookBorrowRepository extends JpaRepository<Borrowed_book_records, Long>, JpaSpecificationExecutor<Borrowed_book_records> {

    @Query("SELECT FUNCTION('DATE', br.borrowedAt) as date, COUNT(br) as count " +
            "FROM Borrowed_book_records br " +
            "WHERE br.borrowedAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', br.borrowedAt)")
    List<Map<String, Long>> getBorrowingTrendsBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query(value = "Select b.title as bookTitle, COUNT(*) as BorrowBookCount " + "From borrowed_books br JOIN books b ON br.book_id = b.id " + "GROUP BY b.id ORDER BY BorrowBookCount DESC LIMIT :limit", nativeQuery = true)
    List<Map<String,Object>> findMostBorrowedBooks(int limit);

    @Query("SELECT b FROM Borrowed_book_records b JOIN FETCH b.book JOIN FETCH b.user WHERE b.id = :id")
    Optional<Borrowed_book_records> findByIdWithBookAndUser(@Param("id") Long id);

    @Query(value = "SELECT * FROM borrowed_books br JOIN book b ON b.id = br.book_id WHERE br.user_id = :userId", nativeQuery = true)
    Optional<BorrowBooksDTO> findBorrowBookByUserId(@Param("userId") Long userId); //! This is Native Sql Query.
    /*@Query("SELECT br FROM Borrowed_book_records br JOIN FETCH br.book JOIN FETCH br.user WHERE br.user.id = :userId")
    Optional<Borrowed_book_records> findBorrowBookByUserId(@Param("userId") Long userId);*/  //! This is Follow JPQL logic.


    @Query("SELECT b FROM Borrowed_book_records b " +
            "JOIN b.book bk " +
            "WHERE b.user.id = :userId " +
            "AND (LOWER(bk.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(bk.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Borrowed_book_records> findByUserIdAndKeyword(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            Pageable pageable
    );



}
