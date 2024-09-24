package com.pard.root.folder.repo;

import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CategoryRepo extends JpaRepository<Category, Long> {

    List<Category> findByUserId(UUID userId);

    @Query("select c from Category c where c.user.id = :userId AND c.title like %:titlePart%")
    List<Category> findByUserIdAndNameContaining(@Param("userId") UUID userId, @Param("titlePart") String titlePart);

    Category findbyId(Long categoryId);
}
