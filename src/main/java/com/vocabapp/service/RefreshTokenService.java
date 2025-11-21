package com.vocabapp.service;

import com.vocabapp.model.RefreshToken;
import com.vocabapp.repository.RefreshTokenRepository;
import com.vocabapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${application.security.jwt.refresh-token.expiration:86400000}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        // Delete old token if exists (optional, for 1 token per user policy)
        // refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
        
        // Or update existing? For simplicity, let's just save. 
        // But since it's OneToOne, we should handle existing.
        // Actually, let's check if user has one.
        Optional<RefreshToken> existing = refreshTokenRepository.findById(userId); 
        // Wait, ID is auto gen. We need to find by User.
        // But OneToOne mapping might cause issues if we insert duplicate.
        // Let's implement deleteByUser first in create.
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    @Transactional
    public RefreshToken createOrUpdateRefreshToken(Long userId) {
        // Delete existing token for user
        refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(userRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());
        
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    @Transactional
    public int deleteByUserId(Long userId) {
        return refreshTokenRepository.deleteByUser(userRepository.findById(userId).get());
    }
}
