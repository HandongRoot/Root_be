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


    @PostMapping("/save/{userId}/{categoryId}")
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

//    @PatchMapping("/change/{contentId}/{afterCategoryId}")
//    public ResponseEntity<?> changeCategory(@PathVariable Long contentId, @PathVariable Long afterCategoryId) {
//        try {
//            contentService.changeCategory(contentId, afterCategoryId);
//            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
//        }
//    }
//
//    @DeleteMapping("/delete/{contentId}")
//    public ResponseEntity<?> deleteContent(@PathVariable Long contentId) {
//        try {
//            contentService.deleteContent(contentId);
//            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
//        }
//        catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
//        }
//    }
}
