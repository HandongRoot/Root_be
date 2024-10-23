package com.pard.root.content.service;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.service.CategoryService;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentService {

    private final ContentRepository contentRepository;
    private final CategoryService categoryService;
    private final UserService userService;



    public void saveContent(Long categoryId, UUID userId,ContentCreateDto dto){
        Category category = categoryService.findById(categoryId);
        User user = userService.findById(userId);


        contentRepository.save(Content.toEntity(category, user, dto));
        categoryService.incrementContentCount(categoryId);
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

    @Transactional
    public void changeCategory(Long contentId, Long categoryId) {

        Category category = categoryService.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found with id: " + categoryId);
        }

        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));

        if (content.getCategory() != null && content.getCategory().getId().equals(categoryId)) {
            throw new IllegalArgumentException("Content is already in the selected category.");
        }

        if(checkToUserId(content.getUser().getId(), category.getUser().getId())){
            categoryService.decrementContentCount(Objects.requireNonNull(content.getCategory()).getId());
            content.changeCategory(category);
            categoryService.incrementContentCount(categoryId);
            contentRepository.save(content);
        }
        else throw new AccessDeniedException("User does not have access to this category.");
    }

    @Transactional
    public void updateTitle(UUID userId,Long contentId, ContentUpdateDto dto) {
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
