package com.pard.root.auth.token.controller;

import com.pard.root.auth.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/token")
@Slf4j
@Tag(name = "Token API", description = "액세스 토큰 관련 API")
public class TokenController
{
    private final TokenService tokenService;

    @PostMapping("/refreshAccessToken")
    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급합니다.")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        try {
            return ResponseEntity.ok(tokenService.refreshAccessToken(requestBody));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
