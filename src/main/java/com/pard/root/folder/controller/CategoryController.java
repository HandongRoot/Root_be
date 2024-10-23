package com.pard.root.folder.controller;


import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.dto.CategoryUpdateDto;
import com.pard.root.folder.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping("/title")
    public ResponseEntity<?> saveCategory(@RequestBody CategoryCreateDto dto) {
        try {
            // 카테고리 저장 로직
            categoryService.save(dto);
            return ResponseEntity.ok("Category saved successfully"); // 성공 시 OK(200) 반환
//        } catch (CategoryAlreadyExistsException e) {
//            log.error("Category already exists: {}", dto.getTitle(), e);
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category already exists");
//        } catch (InvalidCategoryException e) {
//            log.error("Invalid category data: {}", dto, e);
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid category data");
        } catch (Exception e) {
            log.error("An unexpected error occurred while saving the category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to save category due to an internal error");
        }
    }

    @GetMapping("/findAll/{userId}")
    public ResponseEntity<?> getAllCategories(@PathVariable UUID userId) {
        try {
            List<CategoryReadDto> readDto = categoryService.findAll(userId);
            return ResponseEntity.ok(readDto);
        }
        catch (Exception e) {
            log.error("An unexpected error occurred while getting all categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get all categories");
        }
    }

    @GetMapping("/search/{userId}")
    public ResponseEntity<?> searchCategory(@RequestParam String keyword, @PathVariable UUID userId) {
        try {
            List<CategoryReadDto> readDto = categoryService.searchCategoryList(userId, keyword);
            return ResponseEntity.ok(readDto);
        }
        catch (Exception e) {
            log.error("An unexpected error occurred while getting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get categories");
        }
    }

    @PatchMapping("/update/title/{userId}/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID userId, @PathVariable Long categoryId, @RequestBody CategoryUpdateDto dto) {
        try {
            categoryService.updateTitle(categoryId, userId, dto);
            return ResponseEntity.ok("Category updated successfully");
        } catch (Exception e) {
            log.error("An unexpected error occurred while updating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update category");
        }
    }
}
