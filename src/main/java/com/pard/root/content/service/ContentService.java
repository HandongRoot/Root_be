package com.pard.root.content.service;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.content.dto.ContentWithListCategoryReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.entity.ContentCategory;
import com.pard.root.content.repo.ContentCategoryRepository;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.exception.CustomException;
import com.pard.root.exception.ExceptionCode;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.service.CategoryService;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {
    private final ContentCategoryRepository contentCategoryRepository;
    private final ContentRepository contentRepository;
    private final CategoryService categoryService;
    private final UserService userService;


    public void saveContent(UUID userId, ContentCreateDto dto, Long categoryId){
        User user = userService.findById(userId);

        if (categoryId == null){
            contentRepository.save(Content.toEntity(user, dto));
        }
        else {
            Content content = contentRepository.save(Content.toEntity(user, dto));
            Category category = categoryService.findById(categoryId);

            if (checkToUserId(content.getUser().getId(), category.getUser().getId())) {
                ContentCategory contentCategory = new ContentCategory(content, category);
                contentCategoryRepository.save(contentCategory);
                categoryService.incrementContentCount(categoryId);
            } else {
                throw new CustomException(ExceptionCode.AUTHENTICATION_REQUIRED);
            }
        }
    }

    public List<ContentReadDto> findByCategoryId(Long categoryId, UUID userId ){
        Category category = categoryService.findById(categoryId);
        UUID userIdInCategory = category.getUser().getId();
        if(checkToUserId(userId, userIdInCategory)){
            User user = userService.findById(userId);
            List<Content> contents = contentRepository.findContentsByUserAndCategory(user, category);
            CategoryReadDto categoryReadDto = new CategoryReadDto(category);
            return contents.stream()
                    .map(content -> new ContentReadDto(content, categoryReadDto))
                    .toList();
        }
        else throw new CustomException(ExceptionCode.AUTHENTICATION_REQUIRED);
    }

    public List<ContentReadDto> findNextPageByUser(UUID userId, Long contentId){
        User user = userService.findById(userId);
        Pageable pageable = PageRequest.of(0, 25, Sort.by(Sort.Direction.DESC, "id"));
        List<Content> contents = contentRepository.findNextPageByUser(user, contentId, pageable);
        return contents.stream()
                .map(ContentReadDto::new)
                .toList();
    }

    public List<ContentWithListCategoryReadDto> findByUserIdAndTitleContains (UUID userId, String title){
        User user = userService.findById(userId);
        List<Content> contents = contentRepository.findByUserAndTitleContains(user, title);

        if (contents.isEmpty()) {
            return new ArrayList<>();
        } else {
            return contents.stream()
                    .map(content -> {
                        List<CategoryReadDto> categories = contentCategoryRepository.findByContent(content).stream()
                                .map(contentCategory -> new CategoryReadDto(contentCategory.getCategory()))
                                .toList();
                        return new ContentWithListCategoryReadDto(content, categories);
                    })
                    .toList();
        }
    }

    @Transactional
    public void addCategoryToContent(Long[] contentIds, Long categoryId, UUID userId) {
        Category category = categoryService.findById(categoryId);

        List<Content> contents = Arrays.stream(contentIds)
                .map(id -> contentRepository.findById(id)
                        .orElseThrow(() -> new CustomException(ExceptionCode.CONTENT_NOT_FOUND)))
                .toList();

        for (Content content : contents) {
            boolean exists = contentCategoryRepository.existsByContentAndCategory(content, category);
            if (exists) {
                continue;
            }

            if (checkToUserId(content.getUser().getId(), userId)) {
                if (checkToUserId(content.getUser().getId(), category.getUser().getId())) {
                    ContentCategory contentCategory = new ContentCategory(content, category);
                    contentCategoryRepository.save(contentCategory);
                    categoryService.incrementContentCount(categoryId);
                } else {
                    throw new CustomException(ExceptionCode.UNAUTHORIZED_ACCESS);
                }
            }
        }
    }

    @Transactional
    public boolean changeCategoryToContent(Long contentId, Long beforeCategoryId, Long afterCategoryId, UUID userId) {
        Category afterCategory = (afterCategoryId != 0) ? categoryService.findById(afterCategoryId) : null;
        Category beforeCategory = categoryService.findById(beforeCategoryId);

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() ->new CustomException(ExceptionCode.CONTENT_NOT_FOUND));

        if (afterCategory == beforeCategory) {
            return false;
        }

        if (checkToUserId(content.getUser().getId(), userId)) {
            contentCategoryRepository.deleteByContentAndCategory(content, beforeCategory);

            if (afterCategory != null) {
                boolean exists = contentCategoryRepository.existsByContentAndCategory(content, afterCategory);
                if (!exists) {
                    contentCategoryRepository.save(new ContentCategory(content, afterCategory));
                    categoryService.incrementContentCount(afterCategoryId);
                }
            }
            categoryService.decrementContentCount(beforeCategoryId);
        }
        return true;
    }


    @Transactional
    public void updateTitle(UUID userId, Long contentId, ContentUpdateDto dto) {
        try{
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.CONTENT_NOT_FOUND));

            if (checkToUserId(userId, content.getUser().getId())){
                content.updateTitle(dto);
                contentRepository.save(content);
            } else throw new CustomException(ExceptionCode.UNAUTHORIZED_ACCESS);
        } catch (Exception e){
            throw new CustomException(ExceptionCode.CONTENT_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteContent(Long contentId, UUID userId) {
        try{
            Content content = contentRepository.findById(contentId)
                    .orElseThrow(() -> new CustomException(ExceptionCode.CONTENT_NOT_FOUND));

            UUID userIdInContent = content.getUser().getId();

            if (checkToUserId(userId, userIdInContent)) {
                List<ContentCategory> contentCategories = contentCategoryRepository.findByContent(content);

                for (ContentCategory contentCategory : contentCategories) {
                    categoryService.decrementContentCount(contentCategory.getCategory().getId());
                }

                contentCategoryRepository.deleteByContent(content);
                contentRepository.delete(content);
            } else {
                throw new CustomException(ExceptionCode.UNAUTHORIZED_ACCESS);
            }
        } catch (Exception e){
            throw new CustomException(ExceptionCode.CONTENT_DELETE_FAILED);
        }
    }


    private boolean checkToUserId(UUID userId, UUID comparisonId) {
        return userId.equals(comparisonId);
    }
}
