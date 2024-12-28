package com.pard.root.token.entity;

import jakarta.persistence.*;
import lombok.*;

import java.security.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "refresh_token")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "provider_id")
    private String providerId;

    @Column(nullable = false, name = "refresh_token")
    private String refreshToken;
}
