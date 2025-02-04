package com.pard.root.oauth.service;

import com.pard.root.oauth.helper.constants.SocialLoginType;
import com.pard.root.oauth.service.social.SocialOauth;
import com.pard.root.token.service.TokenService;
import com.pard.root.user.entity.User;
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
    private final HttpServletResponse response;
    private final UserService userService;
    private final TokenService tokenService;

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
        tokenService.saveOrUpdateRefreshToken((String) userInfo.get("sub"), (String) token.get("refresh_token"));

        UUID userId = userService.findByProviderId(providerId).orElseThrow().getId();

        Map<String, Object> returnValue = new HashMap<>();
        returnValue.put("user_id", userId);
        returnValue.put("access_token", token.get("access_token"));

        return returnValue;
    }

    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 SocialLoginType 입니다."));
    }
}
