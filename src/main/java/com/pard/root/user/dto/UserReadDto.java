package com.pard.root.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.pard.root.user.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Text;

import java.util.UUID;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserReadDto {
    private UUID id;
    private String name;
    private String email;
    private String pictureUrl;
    private String provider;
    private String providerId;

    public UserReadDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.pictureUrl = user.getPictureUrl();
        this.provider = user.getProvider();
        this.providerId = user.getProviderId();
    }

}
