package com.pard.root.user.repo;

import com.pard.root.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByProviderId(@Param("providerId") String providerId);

    @Query(value = "SELECT * FROM Users WHERE provider_id = :providerId", nativeQuery = true)
    Optional<User> findByProviderId(@Param("providerId") String providerId);

}
