package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findById(Long id);

    Boolean existsByUserName(String username);

    Optional<User> findByUserName(String username);

    Optional<User> findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query(value = "SELECT m.username AS memberName, COUNT(b.id) AS borrowCount, " +
            "SUM(CASE WHEN b.return_at IS NULL THEN 1 ELSE 0 END) AS currentBorrowings " +
            "FROM users m LEFT JOIN borrowed_books b ON m.id = b.user_id " +
            "GROUP BY m.id ORDER BY borrowCount DESC", nativeQuery = true)
    List<Map<String, Object>> getUserActivityReport();
}