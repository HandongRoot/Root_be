package com.pard.root.auth.oauth.service;

import com.pard.root.auth.oauth.converter.AppleLoginRequest;
import com.pard.root.auth.oauth.service.social.AppleOauth;
import com.pard.root.helper.constants.SocialLoginType;
import com.pard.root.auth.oauth.service.social.SocialOauth;
import com.pard.root.config.security.service.JwtProvider;
import com.pard.root.auth.token.service.TokenService;
import com.pard.root.user.entity.User;
import com.pard.root.helper.constants.UserRole;
import com.pard.root.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OauthService {
    private final List<SocialOauth> socialOauthList;
    private final AppleOauth appleOauth;
    private final HttpServletResponse response;
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;

    public void request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        String redirectURL = socialOauth.getOauthRedirectURL();
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        Map<String, Object> token = socialOauth.requestAccessToken(code);
        Map<String, Object> userInfo = socialOauth.getUserInfo(token);
        String providerId = (String) userInfo.get("sub");

        log.info(providerId);
        if (!userService.existsByProviderId(providerId)) {
            userService.saveUser(userInfo);
        }

        return generateTokens(providerId);
    }

    public void unlink(UUID userId) {
        User user = userService.findById(userId);
        SocialLoginType loginType = SocialLoginType.fromProvider(user.getProvider());
        SocialOauth socialOauth = this.findSocialOauthByType(loginType);
        socialOauth.unlink(user.getProviderId());
    }

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }

    public Map<String, Object> requestAppleAccessToken(AppleLoginRequest request) {
        String providerId = request.getUserIdentifier();

        log.info(providerId);
        if (!userService.existsByProviderId(providerId)) {
            Map<String, Object> userInfo = appleOauth.authenticateWithApple(request);
            userService.saveUser(userInfo);
        }

        return generateTokens(providerId);
    }

    private Map<String, Object> generateTokens(String providerId) {
        User user = userService.findByProviderId(providerId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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
        tokenService.saveOrUpdateRefreshToken(userInfo);

        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("access_token", accessToken);
        returnValue.put("refresh_token", refreshToken);

        return returnValue;
    }
}
