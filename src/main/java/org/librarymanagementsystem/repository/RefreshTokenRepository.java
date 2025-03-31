package org.librarymanagementsystem.repository;

import org.librarymanagementsystem.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String refreshToken);
    void deleteByUserId(Long userId);
    void deleteByToken(String token);
}