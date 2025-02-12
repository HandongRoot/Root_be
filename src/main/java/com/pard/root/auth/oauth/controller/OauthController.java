package com.pard.root.auth.oauth.controller;


import com.pard.root.helper.constants.SocialLoginType;
import com.pard.root.auth.oauth.service.OauthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping(value = "/auth")
@Slf4j
public class OauthController {
    private final OauthService oauthService;

    @GetMapping("/{socialLoginType}")
    @Operation(summary = "소셜 로그인 요청 처리", description = "사용자가 특정 소셜 로그인 타입(구글, 카카오)으로 로그인 요청을 보내면 해당 요청을 OauthService로 넘겨 처리합니다.")
    public void socialLogin(@PathVariable("socialLoginType") SocialLoginType socialLoginType) {
        log.info(">> 사용자로부터 SNS 로그인 요청을 받음 :: {} Social Login", socialLoginType);
        oauthService.request(socialLoginType);
    }

    @GetMapping(value = "/{socialLoginType}/callback")
    @Operation(summary = "소셜 로그인 콜백", description = "소셜 로그인 API 서버(구글, 카카오)로부터 받은 인증 코드를 처리합니다.")
    public Map<String, Object> callback(@PathVariable(name = "socialLoginType") SocialLoginType socialLoginType,
                                        @RequestParam(name = "code") String code) {
        log.info(">> 소셜 로그인 API 서버로부터 받은 code :: {}", code);
        return oauthService.requestAccessToken(socialLoginType, code);
    }
}
