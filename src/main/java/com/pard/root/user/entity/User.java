package com.pard.root.user.entity;

import com.pard.root.utility.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

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

    @Column(name = "name", nullable = false, unique = false)  private String name;

    @Lob
    @Column(name = "user_picture_url", nullable = false, unique = false, columnDefinition = "TEXT")  private String pictureUrl;


    @Column(nullable = false, unique = true)
    @Email(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "이메일 형식이 잘못되었습니다.")
    private String email;

}
