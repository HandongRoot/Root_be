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
    private String name;
    private String email;
    private Boolean termsOfServiceAgrmnt;
    private Boolean privacyPolicyAgrmnt;
    private String pictureUrl;
    private String provider;

    public UserReadDto(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.pictureUrl = user.getPictureUrl();
        this.provider = user.getProvider();
        this.termsOfServiceAgrmnt = user.getTermsOfServiceAgrmnt();
        this.privacyPolicyAgrmnt = user.getPrivacyPolicyAgrmnt();
    }

}
