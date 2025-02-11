package com.pard.root.auth.token.repo;

import com.pard.root.auth.token.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByProviderId(String providerId);

    boolean existsByProviderId(String providerId);
    Optional<RefreshToken> findByToken(String token);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken r WHERE r.providerId = :providerId")
    void deleteByProviderId(String providerId);
}
