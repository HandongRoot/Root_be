package com.pard.root.token.entity;

import com.pard.root.utility.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "blacklisted_token")
public class BlacklistedToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "access_token")
    private String accessToken;

    @Column(nullable = false, name = "expiration_time")
    private Date expirationTime;

    public boolean isExpired() {
        return expirationTime.before(new Date());
    }
}
