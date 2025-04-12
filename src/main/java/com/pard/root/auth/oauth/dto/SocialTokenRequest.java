package com.pard.root.auth.oauth.dto;

import lombok.Getter;

@Getter
public class SocialTokenRequest {
    private String access_token;
    private String refresh_token;
}
