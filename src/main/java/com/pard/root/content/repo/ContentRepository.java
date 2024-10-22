package com.pard.root.content.repo;

import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.folder.entity.Category;
import com.pard.root.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT c " +
            "FROM Content c " +
            "WHERE c.user = :user " +
            "ORDER BY c.id DESC")
    List<Content> findAllByUser(@Param("user") User user);

    @Query("SELECT c " +
            "FROM Content c " +
            "WHERE c.user = :user AND c.title LIKE %:titlePart% " +
            "ORDER BY c.id DESC")
    List<Content> findByUserAndTitleContains(@Param("user") User user, @Param("titlePart") String titlePart);

    @Query("SELECT c " +
            "FROM Content c " +
            "WHERE c.user = :user AND c.category = :category " +
            "ORDER BY c.id DESC ")
    List<Content> findContentsByUserAndCategory(@Param("user") User user, @Param("category") Category category);
}
