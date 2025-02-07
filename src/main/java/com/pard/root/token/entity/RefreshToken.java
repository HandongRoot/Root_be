package com.pard.root.token.entity;

import com.pard.root.utility.BaseTimeEntity;
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
public class RefreshToken extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(unique = true, name = "provider_id")
    private String providerId;

    @Column(nullable = false, name = "refresh_token")
    private String refreshToken;
}
