package com.pard.root.auth.oauth.controller;


import com.pard.root.auth.oauth.dto.AppleLoginRequest;
import com.pard.root.auth.oauth.dto.SocialTokenRequest;
import com.pard.root.auth.token.service.TokenService;
import com.pard.root.helper.constants.SocialLoginType;
import com.pard.root.auth.oauth.service.OauthService;
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
@RequestMapping(value = "/auth")
@Slf4j
@Tag(name = "Oauth2 API", description = "로그인과 Token에 관한 API 처리")
public class OauthController {
    private final OauthService oauthService;
    private final TokenService tokenService;

//    @GetMapping("/{socialLoginType}")
//    @Operation(summary = "소셜 로그인 요청 처리 (웹)", description = "사용자가 특정 소셜 로그인 타입(구글, 카카오)으로 로그인 요청을 보내면 해당 요청을 OauthService로 넘겨 처리합니다.")
//    public void socialLogin(@PathVariable("socialLoginType") SocialLoginType socialLoginType) {
//        oauthService.request(socialLoginType);
//    }

    @PostMapping("/apple")
    @Operation(summary = "에플 로그인", description = "에플 로그인을 처리합니다.")
    public ResponseEntity<?> authenticateWithApple(@RequestBody AppleLoginRequest request) {
        try {
            Map<String, Object> tokens = oauthService.requestAppleAccessToken(request);
            return ResponseEntity.ok(tokens);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{socialLoginType}")
    @Operation(summary = "소셜 로그인 콜백 (앱)", description = "소셜 로그인 API 서버(구글, 카카오)로부터 받은 인증 코드를 처리합니다.")
    public ResponseEntity<Map<String, Object>> appSocialLogin(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                        @RequestBody SocialTokenRequest request) {
        Map<String, Object> tokens = oauthService.requestAppSocialLogin(socialLoginType, request);
        return ResponseEntity.ok().body(tokens);
    }

    @GetMapping(value = "/{socialLoginType}/callback")
    @Operation(summary = "소셜 로그인 콜백", description = "소셜 로그인 API 서버(구글, 카카오)로부터 받은 인증 코드를 처리합니다.")
    public ResponseEntity<Map<String, Object>> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                        @RequestParam(name = "code") String code) {
        Map<String, Object> tokens = oauthService.requestAccessToken(socialLoginType, code);
        return ResponseEntity.ok().body(tokens);
    }

    @PostMapping("/refreshAccessToken")
    @Operation(summary = "액세스 토큰 갱신", description = "리프레시 토큰을 이용하여 새로운 액세스 토큰을 발급합니다.")
    public ResponseEntity<Map<String, Object>> refreshAccessToken(@RequestBody Map<String, String> requestBody) {
        try {
            return ResponseEntity.ok().body(tokenService.refreshAccessToken(requestBody));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
