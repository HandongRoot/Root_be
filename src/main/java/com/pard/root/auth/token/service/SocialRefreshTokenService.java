package com.pard.root.auth.token.service;

import com.pard.root.auth.token.entity.SocialRefreshToken;
import com.pard.root.auth.token.repo.SocialRefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialRefreshTokenService {
    private final SocialRefreshTokenRepository socialRefreshTokenRepository;

    public void createSocialRefreshToken(String providerId,String refreshToken) {
        SocialRefreshToken socialRefreshToken = SocialRefreshToken.builder()
                .providerId(providerId)
                .token(refreshToken)
                .build();

        socialRefreshTokenRepository.save(socialRefreshToken);
    }

    public String getRefreshToken(String providerId) {
        return socialRefreshTokenRepository.findByProviderId(providerId).getToken();
    }

    @Transactional
    public void deleteSocialRefreshToken(String providerId) {
        SocialRefreshToken socialRefreshToken = socialRefreshTokenRepository.findByProviderId(providerId);
        socialRefreshTokenRepository.delete(socialRefreshToken);
    }
}
