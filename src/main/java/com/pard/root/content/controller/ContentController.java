package com.pard.root.content.controller;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.content.service.ContentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }


    @PostMapping("/{userId}/{categoryId}")
    public ResponseEntity<String> saveContent(@PathVariable Long categoryId, @PathVariable UUID userId, @RequestBody ContentCreateDto dto) {
        try {
            contentService.saveContent(categoryId, userId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (InvalidInputException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/{userId}/{categoryId}")
    public ResponseEntity<?> findByCategory(@PathVariable Long categoryId, @PathVariable UUID userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findByCategoryId(categoryId, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/findAll/{userId}")
    public ResponseEntity<?> findAll(@PathVariable UUID userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findAll(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/search/{userId}/title")
    public ResponseEntity<?> findByUserIdAndTitleContains(@PathVariable UUID userId, @RequestParam String title) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findByUserIdAndTitleContains(userId, title));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/change/{contentId}/{afterCategoryId}")
    public ResponseEntity<?> changeCategory(@PathVariable Long contentId, @PathVariable Long afterCategoryId) {
        try {
            contentService.changeCategory(contentId, afterCategoryId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/update/title/{userId}/{contentId}")
    public ResponseEntity<?> updateTitle(@PathVariable UUID userId, @PathVariable Long contentId, @RequestBody ContentUpdateDto dto) {
        try {
            contentService.updateTitle(userId, contentId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @DeleteMapping("/{userId}/{contentId}")
    public ResponseEntity<String> deleteContent(@PathVariable UUID userId, @PathVariable Long contentId) {
        try {
            contentService.deleteContent(contentId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Content deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

}
