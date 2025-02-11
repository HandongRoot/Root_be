package com.pard.root.auth.oauth.service.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pard.root.helper.constants.SocialLoginType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KakaoOauth implements SocialOauth {

    @Value("${oauth.kakao.client-id}")
    private String clientId;
    @Value("${oauth.kakao.client-secret}")
    private String clientSecret;
    @Value("${oauth.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${oauth.kakao.token-uri}")
    private String tokenUri;
    @Value("${oauth.kakao.user-info-uri}")
    private String userInfoUri;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public SocialLoginType type() {
        return SocialLoginType.KAKAO;
    }

    @Override
    public String getOauthRedirectURL() {
        return "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";
    }

    @Override
    public Map<String, Object> requestAccessToken(String code) {
        log.info("Kakao OAuth - Requesting access token with code: {}", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("access_token", jsonNode.get("access_token").asText());
            tokenInfo.put("refresh_token", jsonNode.get("refresh_token").asText());

            return tokenInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao OAuth token response", e);
        }
    }

    @Override
    public Map<String, Object> getUserInfo(Map<String, Object> token) {
        log.info("Kakao OAuth - Fetching user info");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.get("access_token").toString());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(userInfoUri, HttpMethod.GET, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("provider", "kakao");
            userInfo.put("sub", jsonNode.get("id").asText());
            userInfo.put("name", jsonNode.path("properties").path("nickname").asText());
            userInfo.put("email", jsonNode.path("kakao_account").path("email").asText());
            userInfo.put("picture", jsonNode.path("properties").path("profile_image").asText());

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao user info response", e);
        }
    }

}
