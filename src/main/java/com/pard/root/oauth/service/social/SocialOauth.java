package com.pard.root.oauth.service.social;

import com.pard.root.oauth.helper.constants.SocialLoginType;

import java.util.Map;

public interface SocialOauth {
    String getOauthRedirectURL();
    Map<String, Object> requestAccessToken(String code);
    Map<String, Object> getUserInfo(Map<String, Object> token);
    default SocialLoginType type() {
//        if (this instanceof GoogleOauth) {
//            return SocialLoginType.GOOGLE;
//        } else if (this instanceof NaverOauth) {
//            return SocialLoginType.NAVER;
//        }
        if (this instanceof KakaoOauth) {
            return SocialLoginType.KAKAO;
        } else {
            return null;
        }
    }

}
