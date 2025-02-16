package com.pard.root.user.repo;

import com.pard.root.helper.constants.UserState;
import com.pard.root.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    boolean existsByProviderId(@Param("providerId") String providerId);

    @Query(value = "SELECT * FROM users WHERE provider_id = :providerId", nativeQuery = true)
    Optional<User> findByProviderId(@Param("providerId") String providerId);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.userState = :state WHERE u.id = :userId")
    void updateUserState(@Param("userId") UUID userId, @Param("state") UserState state);

}
