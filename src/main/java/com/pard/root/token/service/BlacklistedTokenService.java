package com.pard.root.token.service;

import com.pard.root.config.component.JwtProvider;
import com.pard.root.token.entity.BlacklistedToken;
import com.pard.root.token.repo.BlacklistedTokenRepository;
import com.pard.root.token.repo.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlacklistedTokenService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final JwtProvider jwtProvider;

    // 블랙리스트에 Access Token 추가
    public void addToBlacklist(String accessToken) {
        long expirationTimeMillis = jwtProvider.getExpirationTime(accessToken);
        if (expirationTimeMillis == -1) {
            return;
        }

        BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                .accessToken(accessToken)
                .expirationTime(new Date(expirationTimeMillis))
                .build();

        blacklistedTokenRepository.save(blacklistedToken);
    }

    public boolean isTokenBlacklisted(String accessToken) {
        return blacklistedTokenRepository.existsByAccessToken(accessToken);
    }
}
