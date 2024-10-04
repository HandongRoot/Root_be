package com.pard.root.content.service;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.content.repo.ContentRepository;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.repo.CategoryRepo;
import com.pard.root.folder.service.CategoryService;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import com.pard.root.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
//    public List<ContentReadDto> findAll(UUID userId){
//        User user = userRepository.findById(userId).orElseThrow();;
//        return contentRepository.findAllByUser(user);
//    }
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
