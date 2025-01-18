package com.pard.root.folder.service;

import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.content.service.ContentService;
import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.dto.CategoryUpdateDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.repo.CategoryRepo;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final UserRepository userRepo;
    private final ContentRepository contentRepo;

    public void save(CategoryCreateDto categoryCreateDto) {
        log.info("\uD83D\uDCCD Create Category");
        User user = userRepo.findById(categoryCreateDto.getUserId()).orElse(null);

        categoryRepo.save(Category.toEntity(user, categoryCreateDto.getTitle(), 0));
    }

    public Category findById(Long id) {
        return categoryRepo.findById(id).orElseThrow();
    }

    public List<CategoryReadDto> findAll(UUID userId) {
        log.info("\uD83D\uDCCD Find All Category");

        List<Category> categories = categoryRepo.findByUserId(userId);

        return categories.stream()
                .map(category -> new CategoryReadDto(category, findByCategory(category)))
                .collect(Collectors.toList());
    }

    public List<ContentReadDto> findByCategory(Category category){
        Pageable Toptwo = PageRequest.of(0, 2);
        List<Content> contents = contentRepo.findByCategory(category, Toptwo);

        return contents.isEmpty() ? new ArrayList<>() : contents.stream()
                .map(ContentReadDto::new)
                .collect(Collectors.toList());
    }

    public List<CategoryReadDto> searchCategoryList(UUID userId, String titlePart) {
        log.info("\uD83D\uDCCD Search Category");

        List<Category> categories = categoryRepo.findByUserIdAndNameContaining(userId, titlePart);
        return categories.stream()
                .map(CategoryReadDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTitle(Long categoryId, UUID userId, CategoryUpdateDto dto) {
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        if(category.getUser().getId().equals(userId)) {
            category.updateTitle(dto);
        }
        else {
            throw new RuntimeException("You are not the owner of this category.");
        }
    }

    @Transactional
    public void incrementContentCount(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.incrementCountContents();
        categoryRepo.save(category);
    }

    @Transactional
    public void decrementContentCount(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        category.decrementCountContents();
        categoryRepo.save(category);
    }

    public void deleteCategory(Long categoryId, UUID userId) {
        Category category = findById(categoryId);

        if(category.getUser().getId().equals(userId)) {
            contentRepo.removeCategoryFromContents(category);
            categoryRepo.deleteById(categoryId);
        } else {
            throw new RuntimeException("You are not the owner of this category.");
        }
    }
}
