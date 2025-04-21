package org.librarymanagementsystem.servicesImp;

import lombok.RequiredArgsConstructor;
import org.librarymanagementsystem.exception.ResourceNotFoundException;
import org.librarymanagementsystem.model.RefreshToken;
import org.librarymanagementsystem.model.User;
import org.librarymanagementsystem.repository.RefreshTokenRepository;
import org.librarymanagementsystem.repository.UserRepository;
import org.librarymanagementsystem.services.RefreshTokenService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImp implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    @Override
    public void deleteByUserId(Long id) {
        refreshTokenRepository.deleteByUserId(id);
    }

    @Override
    public Optional<RefreshToken> findByToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken);
    }

    @Transactional
    @Override
    public void deleteByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token", "not found"));
        refreshTokenRepository.delete(refreshToken);
    }
}
