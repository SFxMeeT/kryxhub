package com.kryxhub.kryxhub.service;

import com.kryxhub.kryxhub.entity.RefreshTokenEntity;
import com.kryxhub.kryxhub.entity.UserEntity;
import com.kryxhub.kryxhub.repository.RefreshTokenRepository;
import com.kryxhub.kryxhub.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository usersRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository usersRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usersRepository = usersRepository;
    }

    public RefreshTokenEntity createRefreshToken(String email) {
        UserEntity user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(OffsetDateTime.now().plusDays(7));

        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().isBefore(OffsetDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(UserEntity user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
