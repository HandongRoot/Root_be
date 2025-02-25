package com.pard.root.auth.token.repo;

import com.pard.root.auth.token.entity.SocialRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialRefreshTokenRepository extends JpaRepository<SocialRefreshToken, Long> {

    SocialRefreshToken findByProviderId(String providerId);
}
