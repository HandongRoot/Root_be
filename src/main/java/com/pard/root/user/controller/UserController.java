package com.pard.root.user.controller;

import com.pard.root.auth.oauth.service.OauthService;
import com.pard.root.user.dto.UserReadDto;
import com.pard.root.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {
    private final UserService userService;
    private final OauthService oauthService;

    public UserController(UserService userService, OauthService oauthService) {
        this.userService = userService;
        this.oauthService = oauthService;
    }

    @GetMapping("/{userId}")
    @Operation(summary = "User 정보보기", description = "해당 유저의 정보를 보는 방법")
    public ResponseEntity<UserReadDto> findById(@PathVariable UUID userId) {
//            checkValidate(userId);
        UserReadDto userReadDto = userService.findByUserId(userId);
        return ResponseEntity.ok(userReadDto);
    }

    @PostMapping("/logout/{userId}")
    @Operation(summary = "로그 아웃", description = "현재 인증된 사용자를 로그아웃합니다.")
    public ResponseEntity<String> logout(HttpServletRequest request, @PathVariable UUID userId) {
//            checkValidate(userId);
        return userService.logout(request);
    }

    @DeleteMapping("/{userId}")
    @Operation(summary = "회원 탈퇴", description = "유저가 탈퇴합니다. (안돼~~~~~~).")
    public ResponseEntity<String> deleteUser(HttpServletRequest request ,@PathVariable UUID userId) {
//            checkValidate(userId);
        return CompletableFuture.runAsync(() -> oauthService.unlink(userId))
                .thenApply(v -> userService.deleteUser(request, userId))
                .exceptionally(ex -> ResponseEntity.badRequest().body("Error: " + ex.getMessage()))
                .join();
    }

    private void checkValidate(UUID userId){
//        SecurityUtil.validateUserAccess(userId);
    }
}
