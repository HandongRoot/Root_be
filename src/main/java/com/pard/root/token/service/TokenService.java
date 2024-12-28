package com.pard.root.token.service;

import com.pard.root.token.entity.RefreshToken;
import com.pard.root.token.repo.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveOrUpdateRefreshToken(String providerId, String newRefreshToken) {
        if (providerId == null || providerId.isEmpty()) {
            throw new IllegalArgumentException("Provider ID cannot be null or empty");
        }

        Optional<RefreshToken> existingToken = tokenRepository.findByProviderId(providerId);

        if (existingToken.isPresent()) {
            if (!existingToken.get().getRefreshToken().equals(newRefreshToken)) {
                RefreshToken refreshTokenEntity = existingToken.get();
                refreshTokenEntity = RefreshToken.builder()
                        .id(refreshTokenEntity.getId())
                        .providerId(providerId)
                        .refreshToken(newRefreshToken)
                        .build();
                tokenRepository.save(refreshTokenEntity);
            }
        } else {
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .providerId(providerId)
                    .refreshToken(newRefreshToken)
                    .build();
            tokenRepository.save(refreshTokenEntity);
        }
    }


}
