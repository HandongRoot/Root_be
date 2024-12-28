package com.pard.root.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDto {
    private String name;
    private String pictureUrl;
    private String email;
    private String provider;
    private String providerId;
}
