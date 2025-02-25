package com.pard.root.auth.oauth.converter;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppleLoginRequest {
    private String authorizationCode;  // Apple에서 발급한 Authorization Code
    private String identityToken;      // Apple에서 발급한 JWT (ID Token)
    private String userIdentifier;     // Apple의 고유 사용자 ID (sub 값)
    private Map<String, String> fullName;
}
