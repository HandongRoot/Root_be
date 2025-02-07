package com.pard.root.token.repo;

import com.pard.root.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByProviderId(String providerId);

    boolean existsByProviderId(String providerId);
}
