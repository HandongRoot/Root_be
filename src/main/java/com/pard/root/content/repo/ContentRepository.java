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

    @Query("SELECT c " +
            "FROM Content c " +
            "WHERE c.category = :category " +
            "ORDER BY c.id DESC ")
    List<Content> findByCategory(@Param("category") Category category, Pageable pageable);

    @Transactional
    @Modifying
    @Query("UPDATE Content c SET c.category = NULL WHERE c.category = :category")
    void removeCategoryFromContents(@Param("category") Category category);
}
