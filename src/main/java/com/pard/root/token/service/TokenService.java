package com.pard.root.token.service;

import com.pard.root.token.entity.RefreshToken;
import com.pard.root.token.repo.TokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveOrUpdateRefreshToken(Map<String, Object> userInfo) {
        if (userInfo.get("sub") == null) {
            throw new IllegalArgumentException("Provider ID cannot be null or empty");
        }

        Optional<RefreshToken> existingToken = tokenRepository.findByProviderId(userInfo.get("sub").toString());

        if (existingToken.isPresent()) {
            if (!existingToken.get().getRefreshToken().equals(userInfo.get("refresh_token"))) {
                RefreshToken refreshTokenEntity = existingToken.get();
                refreshTokenEntity = RefreshToken.builder()
                        .id(refreshTokenEntity.getId())
                        .providerId((String) userInfo.get("sub"))
                        .refreshToken((String) userInfo.get("refresh_token"))
                        .email((String) userInfo.get("email"))
                        .name((String) userInfo.get("name"))
                        .build();
                tokenRepository.save(refreshTokenEntity);
            }
        } else {
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .providerId((String) userInfo.get("sub"))
                    .refreshToken((String) userInfo.get("refresh_token"))
                    .email((String) userInfo.get("email"))
                    .name((String) userInfo.get("name"))
                    .build();
            tokenRepository.save(refreshTokenEntity);
        }
    }


}
