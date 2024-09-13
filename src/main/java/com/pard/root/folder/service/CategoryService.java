package com.pard.root.folder.service;

import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.repo.CategoryRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public void save(CategoryCreateDto categoryCreateDto) {
        log.info("\uD83D\uDCCD Create Category");
        categoryRepo.save(Category.toEntity(categoryCreateDto));
    }

    public List<CategoryReadDto> findAll(UUID userId) {
        log.info("\uD83D\uDCCD Find All Category");

        List<Category> categories = categoryRepo.findByUserId(userId);

        return categories.stream()
                .map(CategoryReadDto::new)
                .collect(Collectors.toList());
    }

    public List<CategoryReadDto> searchCategoryList(UUID userId, String titlePart) {
        log.info("\uD83D\uDCCD Search Category");

        return categoryRepo.findByUserIdAndNameContaining(userId, titlePart);
    }
}
