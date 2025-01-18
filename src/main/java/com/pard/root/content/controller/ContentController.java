package com.pard.root.content.controller;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/content")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }


    @PostMapping("/{userId}")
    @Operation(summary = "content 등록 기능", description = "해당 유저가 category 속에 content 생성")
    public ResponseEntity<String> saveContent(@PathVariable UUID userId, @RequestBody ContentCreateDto dto) {
        try {
            contentService.saveContent(userId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        } catch (InvalidInputException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/find/{userId}/{categoryId}")
    @Operation(summary = "Category 내에서 Contents 불러오기 기능", description = "해당 유저의 Category 속에 담겨있는 Content를 불러오기")
    public ResponseEntity<?> findByCategory(@PathVariable Long categoryId, @PathVariable UUID userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findByCategoryId(categoryId, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/findAll/{userId}")
    @Operation(summary = "전체 Contents 불러오기", description = "해당 유저의 모든 Contents 를 불러온다.")
    public ResponseEntity<?> findAll(@PathVariable UUID userId) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findAll(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/search/{userId}")
    @Operation(summary = "특정 Content 검색 기능", description = "Param({userId}?title={data}) 값으로 해당 유저의 contents 를 검색한다.")
    public ResponseEntity<?> findByUserIdAndTitleContains(@PathVariable UUID userId, @RequestParam String title) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findByUserIdAndTitleContains(userId, title));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/change/{categoryId}")
    @Operation(summary = "Content 의 Category 변경 기능", description = "해당 Content가 속한 Category(from)에서 afterCategoryId(to)를 받아 그 category로 변경.")
    public ResponseEntity<?> changeCategory(@RequestBody Long[] contentIds, @PathVariable Long categoryId) {
        try {
            contentService.changeCategory(contentIds, categoryId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/update/title/{userId}/{contentId}")
    @Operation(summary = "Content의 이름 변경 기능", description = "해당 유저의 Content의 이름을 바꾸도록 한다.")
    public ResponseEntity<?> updateTitle(@PathVariable UUID userId, @PathVariable Long contentId, @RequestBody ContentUpdateDto dto) {
        try {
            contentService.updateTitle(userId, contentId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @DeleteMapping("/{userId}/{contentId}")
    @Operation(summary = "Content 삭제 기능", description = "해당 유저가 가지고 있는 Content 의 Id 값으로 Content 삭제")
    public ResponseEntity<String> deleteContent(@PathVariable UUID userId, @PathVariable Long contentId) {
        try {
            contentService.deleteContent(contentId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Content deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

}
