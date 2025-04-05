package com.pard.root.auth.oauth.dto;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppleLoginRequest {
    private String authorizationCode;
    private String identityToken;
    private String userIdentifier;
    private Map<String, String> fullName;
}
