package com.pard.root.user.controller;

import com.pard.root.auth.oauth.service.OauthService;
import com.pard.root.user.dto.UserAccessDto;
import com.pard.root.user.dto.UserReadDto;
import com.pard.root.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {
    private final UserService userService;
    private final OauthService oauthService;

    @GetMapping("")
    @Operation(summary = "User 정보보기", description = "해당 유저의 정보를 보는 방법")
    public ResponseEntity<UserReadDto> findById(@AuthenticationPrincipal UUID userId) {
//            checkValidate(userId);
        UserReadDto userReadDto = userService.findByUserId(userId);
        return ResponseEntity.ok(userReadDto);
    }

    @PostMapping("/argmnt")
    @Operation(summary = "User argument 저장", description = "개인 정보 처리 동의, 이용약관 동의 받아야해")
    public ResponseEntity<?> saveArgmnts(@AuthenticationPrincipal UUID userId, @RequestBody UserAccessDto userAccessDto) {
        userService.saveAgrnmt(userId, userAccessDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    @Operation(summary = "로그 아웃", description = "현재 인증된 사용자를 로그아웃합니다.")
    public ResponseEntity<?> logout(HttpServletRequest request, @AuthenticationPrincipal UUID userId) {
        userService.logout(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("")
    @Operation(summary = "회원 탈퇴", description = "유저가 탈퇴합니다. (안돼~~~~~~).")
    public ResponseEntity<String> deleteUser(HttpServletRequest request ,@AuthenticationPrincipal UUID userId) {
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
