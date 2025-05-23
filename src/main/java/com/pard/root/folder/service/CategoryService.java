package com.pard.root.folder.service;

import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.entity.ContentCategory;
import com.pard.root.content.repo.ContentCategoryRepository;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.exception.CustomException;
import com.pard.root.exception.ExceptionCode;
import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.dto.CategoryUpdateDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.repo.CategoryRepo;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
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
    private final ContentRepository contentRepo;
    private final UserService userService;
    private final ContentCategoryRepository contentCategoryRepo;

    public Long save(UUID userId, CategoryCreateDto categoryCreateDto) {
        User user = userService.findById(userId);
//        SecurityUtil.validateUserAccess(categoryCreateDto.getUserId());
        return categoryRepo.save(Category.toEntity(user, categoryCreateDto.getTitle(), 0)).getId();
    }

    public Category findById(Long id) {
        return categoryRepo.findById(id).orElseThrow(() -> new CustomException(ExceptionCode.CATEGORY_NOT_FOUND));
    }

    public List<CategoryReadDto> findAll(UUID userId) {
        List<Category> categories = categoryRepo.findByUserId(userId);

        return categories.stream()
                .map(category -> new CategoryReadDto(category, findByCategory(category)))
                .collect(Collectors.toList());
    }

    public List<ContentReadDto> findByCategory(Category category){
        Pageable Toptwo = PageRequest.of(0, 2);
        List<Content> contents = contentRepo.findByCategoryWithPageable(category, Toptwo);

        return contents.isEmpty() ? new ArrayList<>() : contents.stream()
                .map(ContentReadDto::new)
                .collect(Collectors.toList());
    }

    public List<CategoryReadDto> searchCategoryList(UUID userId, String titlePart) {
        List<Category> categories = categoryRepo.findByUserIdAndNameContaining(userId, titlePart);
        return categories.stream()
                .map(CategoryReadDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateTitle(Long categoryId, UUID userId, CategoryUpdateDto dto) {
        Category category = categoryRepo.findById(categoryId).orElseThrow();
        if(checkToUserId(userId, category.getUser().getId())) {
            category.updateTitle(dto);
        } else throw new CustomException(ExceptionCode.UNAUTHORIZED_ACCESS);
    }

    @Transactional
    public void incrementContentCount(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CATEGORY_NOT_FOUND));

        category.incrementCountContents();
        categoryRepo.save(category);
    }

    @Transactional
    public void decrementContentCount(Long categoryId) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new CustomException(ExceptionCode.CATEGORY_NOT_FOUND));

        category.decrementCountContents();
        categoryRepo.save(category);
    }

    @Transactional
    public void deleteCategory(Long categoryId, UUID userId) {
        Category category = findById(categoryId);

        if (checkToUserId(userId, category.getUser().getId())) {
            List<ContentCategory> contentCategories = contentCategoryRepo.findByCategory(category);

            for (ContentCategory contentCategory : contentCategories) {
                decrementContentCount(contentCategory.getCategory().getId());
            }

            categoryRepo.delete(category);
        } else {
            throw new CustomException(ExceptionCode.CATEGORY_DELETE_FAILED);
        }
    }

    private boolean checkToUserId(UUID userId, UUID comparisonId) {
        return userId.equals(comparisonId);
    }
}
