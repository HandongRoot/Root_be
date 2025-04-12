package com.pard.root.auth.oauth.service;

import com.pard.root.auth.oauth.dto.AppleLoginRequest;
import com.pard.root.auth.oauth.dto.SocialTokenRequest;
import com.pard.root.auth.oauth.service.social.AppleOauth;
import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.service.ContentService;
import com.pard.root.exception.CustomException;
import com.pard.root.exception.ExceptionCode;
import com.pard.root.helper.constants.SocialLoginType;
import com.pard.root.auth.oauth.service.social.SocialOauth;
import com.pard.root.auth.token.service.TokenService;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
    private final ContentService contentService;

    @Value("${root.start.image}")
    private String startImage;

    @Value("${root.start.title}")
    private String startTitle;

    @Value("${root.start.url}")
    private String startUrl;

    /**
     * 소셜로그인의 타일을 알아보는 method
     * @param socialLoginType 소셜 로그인 제공자의 유형 (예: GOOGLE, KAKAO, NAVER 등)
     */
    public void request(SocialLoginType socialLoginType) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        String redirectURL = socialOauth.getOauthRedirectURL();
        try {
            response.sendRedirect(redirectURL);
        } catch (IOException e) {
            throw new CustomException(ExceptionCode.REDIRECT_FAILED);
        }
    }

    /**
     * 소셜 로그인 타입과 인증 코드를 기반으로 액세스 토큰을 요청하고 사용자 정보를 조회한 후,
     * 기존 사용자라면 토큰을 생성하여 반환하고, 새로운 사용자라면 저장 후 토큰을 반환한다.
     *
     * @param socialLoginType 소셜 로그인 제공자의 유형 (예: GOOGLE, KAKAO, NAVER 등)
     * @param code            OAuth 인증 과정에서 받은 인가 코드 code
     * @return                액세스 토큰과 관련 정보를 포함한 맵 객체
     */
    public Map<String, Object> requestAccessToken(SocialLoginType socialLoginType, String code) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        Map<String, Object> tokenMap = socialOauth.requestAccessToken(code);

        return getOrCreateUserAndGenerateTokens(socialLoginType, tokenMap);
    }

    public Map<String, Object> requestAppSocialLogin(SocialLoginType socialLoginType, SocialTokenRequest dto) {
        Map<String, Object> tokenMap = Map.of(
                "access_token", dto.getAccess_token(),
                "refresh_token", dto.getRefresh_token()
        );
        return getOrCreateUserAndGenerateTokens(socialLoginType, tokenMap);
    }

    /**
     * 사용자의 소셜 로그인 계정을 언링크(unlink)하여 연결을 해제한다.
     * 소셜 로그인 타입을 확인한 후 해당 OAuth 제공자의 언링크 기능을 호출한다.
     *
     * @param userId 언링크할 사용자의 고유 ID(UUID)
     */
    public void unlink(UUID userId) {
        User user = userService.findById(userId);
        SocialLoginType loginType = SocialLoginType.fromProvider(user.getProvider());
        SocialOauth socialOauth = this.findSocialOauthByType(loginType);

        if (socialOauth != null){
            socialOauth.unlink(user.getProviderId());
        } else {
            appleOauth.unlink(user.getProviderId());
        }
    }

    /**
     * Apple 로그인 요청을 처리하여 액세스 토큰을 발급한다.
     * 주어진 Apple 로그인 요청에서 사용자 식별자를 추출하고, 존재하지 않는 경우 새 사용자 정보를 저장한 후 토큰을 생성한다.
     *
     * @param request Apple 로그인 요청 객체
     * @return        액세스 토큰과 관련 정보를 포함한 맵 객체
     */
    public Map<String, Object> requestAppleAccessToken(AppleLoginRequest request) {
        String providerId = request.getUserIdentifier();
        log.info(providerId);

        if (!userService.existsByProviderId(providerId)) {
            Map<String, Object> userInfo = appleOauth.authenticateWithApple(request);
            User newUser = userService.saveUser(userInfo);

            contentService.saveContent(newUser.getId(), ContentCreateDto.builder()
                    .title(startTitle)
                    .linkedUrl(startUrl)
                    .thumbnail(startImage)
                    .build(), null);

            return tokenService.generateTokens(newUser, providerId);
        } else {
            User user = userService.findByProviderId(providerId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
            return tokenService.generateTokens(user, providerId);
        }
    }

    /**
     * SocialLoginType에 맞는 SocialOauth를 반환
     * @param socialLoginType 소셜 로그인 제공자의 유형 (예: GOOGLE, KAKAO, NAVER 등)
     * @return SocialOauth
     */
    private SocialOauth findSocialOauthByType(SocialLoginType socialLoginType) {
        return socialOauthList.stream()
                .filter(x -> x.type() == socialLoginType)
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionCode.NOT_MATCHING_SOCIAL_TYPE));
    }

    /*
      주어진 사용자 정보를 기반으로 액세스 토큰과 리프레시 토큰을 생성한다.
      사용자 상태를 활성(active)으로 업데이트한 후, JWT를 발급하고 저장한다.
      @param user       토큰을 생성할 사용자 객체
     * @param providerId 사용자의 소셜 로그인 제공자 ID
     * @return           생성된 액세스 토큰과 리프레시 토큰을 포함하는 맵 객체
     */
    private Map<String, Object> getOrCreateUserAndGenerateTokens(SocialLoginType socialLoginType, Map<String, Object> tokenMap) {
        SocialOauth socialOauth = this.findSocialOauthByType(socialLoginType);
        Map<String, Object> userInfo = socialOauth.getUserInfo(tokenMap);
        String providerId = (String) userInfo.get("sub");

        if (!userService.existsByProviderId(providerId)) {
            User user = userService.saveUser(userInfo);

            contentService.saveContent(
                    user.getId(),
                    ContentCreateDto.builder()
                            .title(startTitle)
                            .linkedUrl(startUrl)
                            .thumbnail(startImage)
                            .build(),
                    null
            );

            return tokenService.generateTokens(user, providerId);
        } else {
            User user = userService.findByProviderId(providerId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

            return tokenService.generateTokens(user, providerId);
        }
    }
}
