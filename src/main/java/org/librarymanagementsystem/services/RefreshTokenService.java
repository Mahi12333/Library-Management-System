package org.librarymanagementsystem.services;


import org.librarymanagementsystem.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    void deleteByUserId(Long id);
    Optional<RefreshToken> findByToken(String refreshToken);
    void deleteByToken(String token);
}
