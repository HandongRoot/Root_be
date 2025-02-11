package com.pard.root.token.service;

import com.pard.root.config.component.JwtProvider;
import com.pard.root.token.entity.BlacklistedToken;
import com.pard.root.token.repo.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
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

    // 해당 Access Token이 blacklist 인지 확인
    public boolean isTokenBlacklisted(String accessToken) {
        return blacklistedTokenRepository.existsByAccessToken(accessToken);
    }


    // System time 기준 매 정각마다 BlackList token 삭제
    @Scheduled(cron = "0 0 * * * ?")
    public void deleteBlacklistedTokens() {
        blacklistedTokenRepository.deleteByExpirationTimeBefore(new Date());
        log.info("Deleted blacklisted tokens");
    }
}
