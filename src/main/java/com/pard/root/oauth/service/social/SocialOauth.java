package com.pard.root.oauth.service.social;

import com.pard.root.oauth.helper.constants.SocialLoginType;

import java.util.Map;

public interface SocialOauth {
    String getOauthRedirectURL();
    String requestAccessToken(String code);
    Map<String, Object> getUserInfo(String accessToken);
    default SocialLoginType type() {
        if (this instanceof GoogleOauth) {
            return SocialLoginType.GOOGLE;
//        } else if (this instanceof NaverOauth) {
//            return SocialLoginType.NAVER;
//        } else if (this instanceof KakaoOauth) {
//            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }

}
