package com.pard.root.content.service;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.repo.CategoryRepo;
import com.pard.root.folder.service.CategoryService;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final CategoryService categoryService;
    private final UserService userService;


    public void saveContent(UUID userId, ContentCreateDto dto){
        User user = userService.findById(userId);
        contentRepository.save(Content.toEntity(null, user, dto));
    }

    public List<ContentReadDto> findByCategoryId(Long categoryId, UUID userId ){
        Category category = categoryService.findById(categoryId);
        UUID userIdInCategory = category.getUser().getId();
        if(checkToUserId(userId, userIdInCategory)){
            User user = userService.findById(userId);
            List<Content> contents = contentRepository.findContentsByUserAndCategory(user, category);
            return contents.stream()
                    .map(ContentReadDto::new)
                    .toList();
        }
        else throw new AccessDeniedException("User does not have access to this category.");
    }

    public List<ContentReadDto> findAll(UUID userId){
        User user = userService.findById(userId);
        List<Content> contents = contentRepository.findAllByUser(user);
        return contents.stream()
                .map(ContentReadDto::new)
                .toList();
    }

    public List<ContentReadDto> findByUserIdAndTitleContains (UUID userId, String title){
        User user = userService.findById(userId);
        List<Content> contents = contentRepository.findByUserAndTitleContains(user, title);

        if (contents.isEmpty()) {
            return new ArrayList<>();
        } else {
            return contents.stream()
                    .map(content -> {
                        Category category = content.getCategory();
                        CategoryReadDto dto = (category != null) ? new CategoryReadDto(category) : null;
                        return new ContentReadDto(content, dto);
                    })
                    .toList();
        }
    }

    @Transactional
    public void changeCategory(Long[] contentIds, Long categoryId) {

        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }
        log.info("\uD83D\uDCCD Search Category");

        List<Content> contents = Arrays.stream(contentIds)
                .map(id -> contentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Content not found with id: " + id)))
                .toList();
        for (Content content : contents) {
            if (content.getCategory() != null && content.getCategory().getId().equals(categoryId)) {
                throw new IllegalArgumentException("Content with ID " + content.getId() + " is already in the selected category.");
            }

            log.info("\uD83D\uDCCD Search content");
            if (checkToUserId(content.getUser().getId(), category.getUser().getId())) {
                if (content.getCategory() != null) {
                    categoryService.decrementContentCount(content.getCategory().getId());
                    log.info("\uD83D\uDCCD Search decrementContentCount");
                }
                content.changeCategory(category);
                categoryService.incrementContentCount(categoryId);
            } else {
                throw new AccessDeniedException("User does not have access to content ID " + content.getId());
            }
        }
    }

    @Transactional
    public void updateTitle(UUID userId, Long contentId, ContentUpdateDto dto) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        if (checkToUserId(userId, content.getUser().getId())){
            content.updateTitle(dto);
            contentRepository.save(content);
        }
    }

    public void deleteContent(Long contentId, UUID userId){
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("Content not found for id: " + contentId));
        UUID userIdInContent = content.getUser().getId();
        if(checkToUserId(userId, userIdInContent)){
            contentRepository.delete(content);
            categoryService.decrementContentCount(content.getCategory().getId());
        }
        else throw new AccessDeniedException("User does not have access to this category.");

    }

    private boolean checkToUserId(UUID userId, UUID comparisonId) {
        return userId.equals(comparisonId);
    }
}
