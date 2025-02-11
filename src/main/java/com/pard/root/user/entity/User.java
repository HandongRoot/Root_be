package com.pard.root.user.entity;

import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.helper.constants.UserRole;
import com.pard.root.utility.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseTimeEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.BINARY)
    private UUID id;

    @Column(name = "name", nullable = false, unique = false)
    private String name;

    @Lob
    @Column(name = "user_picture_url", nullable = true, unique = false, columnDefinition = "TEXT")
    private String pictureUrl;


    @Column(nullable = true, unique = false)
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "이메일 형식이 잘못되었습니다.")
    private String email;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();

    private boolean is_active;

    private String provider;
    private String providerId;

    @Builder
    public User(String name, String email, Set<UserRole> roles) {
        this.name = name;
        this.email = email;
        this.roles = roles;
    }

    public static User toEntity(UserCreateDto userCreateDto) {
        return User.builder()
                .name(userCreateDto.getName())
                .email(userCreateDto.getEmail())
                .pictureUrl(userCreateDto.getPictureUrl())
                .provider(userCreateDto.getProvider())
                .providerId(userCreateDto.getProviderId())
                .roles(Set.of(UserRole.USER))
                .is_active(true)
                .build();
    }
}
