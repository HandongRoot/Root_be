package com.pard.root.user.dto;

import com.pard.root.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAccessResponseDto {
    private Boolean termsOfServiceAgrmnt;
    private Boolean privacyPolicyAgrmnt;

    public static UserAccessResponseDto from(User user) {
        return UserAccessResponseDto.builder()
                .termsOfServiceAgrmnt(user.getTermsOfServiceAgrmnt())
                .privacyPolicyAgrmnt(user.getPrivacyPolicyAgrmnt())
                .build();
    }
}
