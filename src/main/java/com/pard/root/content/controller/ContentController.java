package com.pard.root.content.controller;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/api/v1/content")
@Tag(name = "Contents API", description = "Contents 관련 API")
public class ContentController {
    private final ContentService contentService;

    public ContentController(ContentService contentService) {
        this.contentService = contentService;
    }


    @PostMapping("/{userId}")
    @Operation(summary = "content 등록 기능", description = "해당 유저가 content 생성")
    public ResponseEntity<String> saveContent(@PathVariable UUID userId, @RequestBody ContentCreateDto dto) {
        try {
//            checkVaildate(userId);
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
//            checkVaildate(userId);
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findByCategoryId(categoryId, userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/findAll/{userId}")
    @Operation(summary = "전체 Contents 불러오기", description = "해당 유저의 모든 Contents 를 불러온다.")
    public ResponseEntity<?> findAll(@PathVariable UUID userId) {
        try {
//            checkVaildate(userId);
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findAll(userId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @GetMapping("/search/{userId}")
    @Operation(summary = "특정 Content 검색 기능", description = "Param({userId}?title={data}) 값으로 해당 유저의 contents 를 검색한다.")
    public ResponseEntity<?> findByUserIdAndTitleContains(@PathVariable UUID userId, @RequestParam String title) {
        try {
//            checkVaildate(userId);
            return ResponseEntity.status(HttpStatus.OK).body(contentService.findByUserIdAndTitleContains(userId, title));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/add/{userId}/{categoryId}")
    @Operation(summary = "Contents 의 Category 추가 기능 (겔러리 용)", description = "해당 Content 여러 개 혹은 1개가 CategoryId(to)를 받아 그 category로 주입.")
    public ResponseEntity<?> addCategoryToContent(@RequestBody Long[] contentIds,@PathVariable UUID userId ,@PathVariable Long categoryId) {
        try {
//            checkVaildate(userId);
            contentService.addCategoryToContent(contentIds, categoryId, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body("Content saved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/change/{userId}/{beforeCategoryId}/{afterCategoryId}")
    @Operation(summary = "Content가 있던 Category의 정보를 바꾸는 기능 (카테고리 용)", description = "해당 Content가 CategoryId(to)를 받아 그 category로 변경, if(CategoryId == 0) 일 시, category에서 빠지게 됨")
    public ResponseEntity<?> changeCategoryToContent(@PathVariable Long afterCategoryId, @PathVariable Long beforeCategoryId, @PathVariable UUID userId, @RequestBody Long contentId){
        try {
//            checkVaildate(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(contentService.changeCategoryToContent(contentId, beforeCategoryId, afterCategoryId, userId));
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    @PatchMapping("/update/title/{userId}/{contentId}")
    @Operation(summary = "Content의 이름 변경 기능", description = "해당 유저의 Content의 이름을 바꾸도록 한다.")
    public ResponseEntity<?> updateTitle(@PathVariable UUID userId, @PathVariable Long contentId, @RequestBody ContentUpdateDto dto) {
        try {
//            checkVaildate(userId);
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
//            checkVaildate(userId);
            contentService.deleteContent(contentId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Content deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    private void checkVaildate(UUID userId){
//        SecurityUtil.validateUserAccess(userId);
    }
}
