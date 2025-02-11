package com.pard.root.user.controller;

import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.user.dto.UserReadDto;
import com.pard.root.user.entity.User;
import com.pard.root.user.service.UserService;
import com.pard.root.utility.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public void createUser(@RequestBody UserCreateDto dto){
        userService.createUser(dto);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "User 정보보기", description = "해당 유저의 정보를 보는 방법")
    public ResponseEntity<UserReadDto> findById(@PathVariable UUID userId) {
        try{
            SecurityUtil.validateUserAccess(userId);
            UserReadDto userReadDto = userService.findByUserId(userId);
            return ResponseEntity.ok(userReadDto);
        } catch (Exception ex){
            log.error(ex.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        try {
            return userService.logout(request);
        } catch (Exception ex){
            return ResponseEntity.badRequest().build();
        }
    }
}
