package com.pard.root.content.service;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.repo.CategoryRepo;
import com.pard.root.folder.service.CategoryService;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
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

        if(checkToUserId(userId, category)){
            User user = userService.findById(userId);
            List<Content> contents = contentRepository.findContentsByUserAndCategory(user, category);
            return contents.stream()
                    .map(ContentReadDto::new)
                    .toList();
        }
        else throw new AccessDeniedException("User does not have access to this category.");
    }

//    public List<ContentReadDto> findAll(UUID userId){
//        User user = userService.findById(userId);
//        return contentRepository.findAllByUser(user);
//    }

    private boolean checkToUserId(UUID userId, Category category) {
        UUID userIdInCategory = category.getUser().getId();
        return userId.equals(userIdInCategory);
    }
//    @Transactional
//    public void changeCategory(Long contentId, Long categoryId) {
//        Category category = categoryRepo.findByCategoryId(categoryId);
//        Content content = contentRepository.findById(contentId)
//                .orElseThrow(() -> new RuntimeException("Content not found with id: " + contentId));
//
//        content.changeCategory(category);
//        contentRepository.save(content);
//    }
//
//
//    public List<ContentReadDto> findbyUserIdAndTitlePart(UUID userId, String titlePart){
//        User user = userRepository.findById(userId).orElseThrow();
//
//        return contentRepository.findByUserAndTitleContains(user, titlePart);
//    }
//
//    @Transactional
//    public void deleteContent (Long contentId){
//        contentRepository.deleteById(contentId);
//    }
}
