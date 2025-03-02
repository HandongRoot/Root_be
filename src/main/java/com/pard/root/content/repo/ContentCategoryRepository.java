package com.pard.root.content.repo;

import com.pard.root.content.entity.Content;
import com.pard.root.content.entity.ContentCategory;
import com.pard.root.folder.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ContentCategoryRepository extends JpaRepository<ContentCategory, Long> {
    List<ContentCategory> findByContent(Content content);
    boolean existsByContentAndCategory(Content content, Category category);

    @Transactional
    @Modifying
    void deleteByContentAndCategory(Content content, Category category);

    List<ContentCategory> findByCategory(Category category);

    @Transactional
    @Modifying
    void deleteByContent(Content content);
}
