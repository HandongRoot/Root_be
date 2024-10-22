package com.pard.root.content.repo;

import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT ContentReadDto(c.id, c.title, c.image, c.linkedUrl) " +
            "FROM Content c " +
            "WHERE c.user = :user " +
            "ORDER BY c.id DESC")
    List<ContentReadDto> findAllByUser(@Param("user") User user);

    @Query("SELECT ContentReadDto(c.id, c.title, c.image, c.linkedUrl) " +
            "FROM Content c " +
            "WHERE c.user = :user AND c.title LIKE %:titlePart% " +
            "ORDER BY c.id DESC")
    List<ContentReadDto> findByUserAndTitleContains(@Param("user") User user, @Param("titlePart") String titlePart);

}
