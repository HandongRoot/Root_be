package com.pard.root.auth.token.service;

import com.pard.root.auth.token.entity.SocialRefreshToken;
import com.pard.root.auth.token.repo.SocialRefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.data.repository.util.ClassUtils.ifPresent;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialRefreshTokenService {
    private final SocialRefreshTokenRepository socialRefreshTokenRepository;

    public void createSocialRefreshToken(String providerId,String refreshToken) {
        Optional<SocialRefreshToken> existingToken =
                Optional.ofNullable(socialRefreshTokenRepository.findByProviderId(providerId));

        existingToken.ifPresent(token -> {
            socialRefreshTokenRepository.deleteById(token.getId());
        });

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
