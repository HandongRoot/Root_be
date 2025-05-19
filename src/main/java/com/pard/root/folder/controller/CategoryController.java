package com.pard.root.folder.controller;


import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryReadDto;
import com.pard.root.folder.dto.CategoryUpdateDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.folder.service.CategoryService;
import com.pard.root.config.security.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/category")
@Tag(name = "Category API", description = "Category 관련 API")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @PostMapping()
    @Operation(summary = "Category 등록 기능", description = "해당 유저가 Category 생성")
    public ResponseEntity<?> saveCategory(@AuthenticationPrincipal UUID userId, @RequestBody CategoryCreateDto dto) {
        try {
            Long id = categoryService.save(userId, dto);
            return ResponseEntity.ok(id);
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

    @GetMapping("/findAll")
    @Operation(summary = "Category 보기 (모든 것)", description = "해당 유저가 가지고 있는 Category를 다 보기")
    public ResponseEntity<?> getAllCategories(@AuthenticationPrincipal UUID userId) {
        try {
//            checkVaildate(userId);
            List<CategoryReadDto> readDto = categoryService.findAll(userId);
            return ResponseEntity.ok(readDto);
        }
        catch (Exception e) {
            log.error("An unexpected error occurred while getting all categories", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get all categories");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Category 검색 기능", description = "Param({userId}?title={data})해당 유저가 같고 있는 Category를 찾는 기능입니다.")
    public ResponseEntity<?> searchCategory(@AuthenticationPrincipal String title, @PathVariable UUID userId) {
        try {
//            checkVaildate(userId);
            List<CategoryReadDto> readDto = categoryService.searchCategoryList(userId, title);
            return ResponseEntity.ok(readDto);
        }
        catch (Exception e) {
            log.error("An unexpected error occurred while getting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get categories");
        }
    }

    @PatchMapping("/update/title/{categoryId}")
    @Operation(summary = "Category's Title 변경 기능", description = "Category의 Id 값으로 Title을 새롭게 변경하는 기능")
    public ResponseEntity<?> updateCategory(@AuthenticationPrincipal UUID userId, @PathVariable Long categoryId, @RequestBody CategoryUpdateDto dto) {
        try {
//            checkVaildate(userId);
            categoryService.updateTitle(categoryId, userId, dto);
            return ResponseEntity.ok("Category updated successfully");
        } catch (Exception e) {
            log.error("An unexpected error occurred while updating category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update category");
        }
    }

    @DeleteMapping("/delete/{categoryId}")
    @Operation(summary = "Category 삭제 기능", description = "Category 의 Id 값으로 Category 삭제")
    public ResponseEntity<?> deleteCategory(@AuthenticationPrincipal UUID userId, @PathVariable Long categoryId) {
        try {
//            checkVaildate(userId);
            categoryService.deleteCategory(categoryId, userId);
            return ResponseEntity.ok("Category deleted successfully");
        } catch (Exception e) {
            log.error("An unexpected error occurred while deleting category", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete category");
        }
    }

    private void checkVaildate(UUID userId){
//        SecurityUtil.validateUserAccess(userId);
    }
}
