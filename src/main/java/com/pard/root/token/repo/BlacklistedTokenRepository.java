package com.pard.root.token.repo;

import com.pard.root.token.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    @Transactional
    void deleteByExpirationTimeBefore(Date expirationTime);

    Optional<BlacklistedToken> findByAccessToken(String accessToken);
    boolean existsByAccessToken(String accessToken);
}
