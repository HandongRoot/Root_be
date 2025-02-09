package com.pard.root.token.service;

import com.pard.root.config.component.JwtProvider;
import com.pard.root.token.entity.RefreshToken;
import com.pard.root.token.repo.TokenRepository;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;
    private final UserService userService;

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

    public Map<String, Object> refreshAccessToken(Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refresh_token");

        if(refreshToken == null) {
            throw new IllegalArgumentException("Refresh token cannot be null");
        }

        if(!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token is expired or invalid. Please log in again.");
        }

        String providerId = jwtProvider.parseToken(refreshToken).getSubject();
        if (!tokenRepository.existsByProviderId(providerId)) {
            throw new RuntimeException("Refresh token does not match. Please log in again.");
        }

        User user = userService.findByProviderId(providerId).orElseThrow(RuntimeException::new);
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());

        String access_token = jwtProvider.generateAccessToken(claims, providerId);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("access_token", access_token);
        userInfo.put("refresh_token", refreshToken);

        return userInfo;
    }
}
