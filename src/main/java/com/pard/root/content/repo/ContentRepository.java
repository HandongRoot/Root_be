package com.pard.root.content.repo;

import com.pard.root.content.entity.Content;
import com.pard.root.folder.entity.Category;
import com.pard.root.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Query("SELECT c " +
            "FROM Content c " +
            "WHERE c.user = :user " +
            "AND (:lastId IS NULL OR c.id < :lastId) " +
            "ORDER BY c.id DESC")
    List<Content> findNextPageByUser(@Param("user") User user, @Param("lastId") Long lastId, Pageable pageable);

    @Query("SELECT c " +
            "FROM Content c " +
            "WHERE c.user = :user AND c.title LIKE %:titlePart% " +
            "ORDER BY c.id DESC")
    List<Content> findByUserAndTitleContains(@Param("user") User user, @Param("titlePart") String titlePart);

    @Query("SELECT c FROM Content c " +
            "JOIN ContentCategory cc ON c = cc.content " +
            "WHERE c.user = :user AND cc.category = :category " +
            "AND (:lastId IS NULL OR c.id < :lastId) "  +
            "ORDER BY c.id DESC")
    List<Content> findContentsByUserAndCategory(@Param("user") User user, @Param("category") Category category, @Param("lastId") Long lastId, Pageable pageable);

    @Query("SELECT c FROM Content c " +
            "JOIN ContentCategory cc ON c = cc.content " +
            "WHERE cc.category = :category " +
            "ORDER BY c.id DESC")
    List<Content> findByCategoryWithPageable(@Param("category") Category category, Pageable pageable);
}
