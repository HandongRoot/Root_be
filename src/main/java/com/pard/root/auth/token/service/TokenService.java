package com.pard.root.auth.token.service;

import com.pard.root.config.security.service.JwtProvider;
import com.pard.root.exception.user.UserNotFoundException;
import com.pard.root.helper.constants.UserRole;
import com.pard.root.auth.token.entity.RefreshToken;
import com.pard.root.auth.token.repo.TokenRepository;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;
    private final UserService userService;
    private final JwtProvider jwtProvider;

    public Map<String, Object> refreshAccessToken(Map<String, String> requestBody) {
        log.info("Received refresh token request: {}", requestBody);
        String refreshToken = requestBody.get("refresh_token");

        if(refreshToken == null) {
            throw new IllegalArgumentException("Refresh token cannot be null");
        }

        RefreshToken storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found. Please log in again."));

        if(!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Refresh token is expired or invalid. Please log in again.");
        }

        tokenRepository.delete(storedToken);

        String providerId = storedToken.getProviderId();
        String newRefreshToken = jwtProvider.generateRefreshToken(providerId);
        User user = userService.findByProviderId(providerId)
                .orElseThrow(() -> new UserNotFoundException(providerId));
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .providerId(providerId)
                .token(newRefreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .build();
        tokenRepository.save(refreshTokenEntity);

        return generateTokens(user, providerId);
    }

    public Map<String, Object> generateTokens(User user, String providerId) {
        userService.updateUserStateToActive(providerId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", user.getRoles().stream()
                .filter(role -> role == UserRole.USER)
                .map(UserRole::getAuthority)
                .toList());

        String accessToken = jwtProvider.generateAccessToken(claims, providerId);
        String refreshToken = jwtProvider.generateRefreshToken(providerId);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sub", providerId);
        userInfo.put("refresh_token", refreshToken);
        this.saveOrUpdateRefreshToken(userInfo);

        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("access_token", accessToken);
        returnValue.put("refresh_token", refreshToken);

        return returnValue;
    }

    private void saveOrUpdateRefreshToken(Map<String, Object> userInfo) {
        if (userInfo.get("sub") == null) {
            throw new IllegalArgumentException("Provider ID cannot be null or empty");
        }

        Optional<RefreshToken> existingToken = tokenRepository.findByProviderId(userInfo.get("sub").toString());

        if (existingToken.isPresent()) {
            if (!existingToken.get().getToken().equals(userInfo.get("refresh_token"))) {
                RefreshToken refreshTokenEntity = existingToken.get();
                refreshTokenEntity = RefreshToken.builder()
                        .id(refreshTokenEntity.getId())
                        .providerId((String) userInfo.get("sub"))
                        .token((String) userInfo.get("refresh_token"))
                        .build();
                tokenRepository.save(refreshTokenEntity);
            }
        } else {
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .providerId((String) userInfo.get("sub"))
                    .token((String) userInfo.get("refresh_token"))
                    .build();
            tokenRepository.save(refreshTokenEntity);
        }
    }
}
