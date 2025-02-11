package com.pard.root.token.service;

import com.pard.root.config.component.JwtProvider;
import com.pard.root.token.entity.RefreshToken;
import com.pard.root.token.repo.TokenRepository;
import com.pard.root.user.entity.User;
import com.pard.root.user.entity.constants.Role;
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
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public void saveOrUpdateRefreshToken(Map<String, Object> userInfo) {
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
                        .email((String) userInfo.get("email"))
                        .name((String) userInfo.get("name"))
                        .build();
                tokenRepository.save(refreshTokenEntity);
            }
        } else {
            RefreshToken refreshTokenEntity = RefreshToken.builder()
                    .providerId((String) userInfo.get("sub"))
                    .token((String) userInfo.get("refresh_token"))
                    .email((String) userInfo.get("email"))
                    .name((String) userInfo.get("name"))
                    .build();
            tokenRepository.save(refreshTokenEntity);
        }
    }

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
        User user = userRepository.findByProviderId(providerId).orElseThrow(RuntimeException::new);
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .providerId(providerId)
                .token(newRefreshToken)
                .email(user.getEmail())
                .name(user.getName())
                .build();
        tokenRepository.save(refreshTokenEntity);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        claims.put("roles", user.getRoles().stream()
                .filter(role -> role == Role.USER)
                .map(Role::getAuthority)
                .toList());

        String access_token = jwtProvider.generateAccessToken(claims, providerId);

        log.info("Generated access providerId: {}",  jwtProvider.parseToken(access_token).getSubject());
        log.info("Generated refreshToken providerId: {}",  jwtProvider.parseToken(refreshToken).getSubject());

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("access_token", access_token);
        userInfo.put("refresh_token", refreshToken);

        return userInfo;
    }

    public void deleteByProviderId(String providerId) {
        tokenRepository.deleteByProviderId(providerId);
    }
}
