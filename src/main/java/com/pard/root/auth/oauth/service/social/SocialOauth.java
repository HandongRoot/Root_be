package com.pard.root.auth.oauth.service.social;

import com.pard.root.helper.constants.SocialLoginType;

import java.util.Map;

public interface SocialOauth {
    String getOauthRedirectURL();
    Map<String, Object> requestAccessToken(String code);
    Map<String, Object> getUserInfo(Map<String, Object> token);
    void unlink(String providerId);
    default SocialLoginType type() {
//        } else if (this instanceof NaverOauth) {
//            return SocialLoginType.NAVER;
//        }
        if (this instanceof KakaoOauth) {
            return SocialLoginType.KAKAO;
//        } else if (this instanceof GoogleOauth) {
//            return SocialLoginType.GOOGLE;
        } else {
            return null;
        }
    }

}
