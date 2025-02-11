package com.pard.root.auth.token.dto;

import lombok.Getter;
import lombok.Setter;

import java.security.Timestamp;

@Getter
@Setter
public class RefreshTokenCreateDto {
    private String refreshToken;
    private String providerId;
}
