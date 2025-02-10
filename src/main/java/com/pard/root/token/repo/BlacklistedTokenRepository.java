package com.pard.root.token.repo;

import com.pard.root.token.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByAccessToken(String accessToken);
    boolean existsByAccessToken(String accessToken);
}
