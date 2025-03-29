package com.pard.root.auth.oauth.service.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pard.root.auth.token.service.SocialRefreshTokenService;
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

    @Value("${sns.kakao.client-id}")
    private String clientId;
    @Value("${sns.kakao.client-secret}")
    private String clientSecret;
    @Value("${sns.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${sns.kakao.token-uri}")
    private String tokenUri;
    @Value("${sns.kakao.user-info-uri}")
    private String userInfoUri;
    @Value("${sns.kakao.unlink-uri}")
    private String unlinkUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final SocialRefreshTokenService socialRefreshTokenService;

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
        } catch (Exception e)  {
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

            String providerId = jsonNode.get("id").asText();
            String refreshToken = token.get("refresh_token").toString() == null ? "" : token.get("refresh_token").toString();
            socialRefreshTokenService.createSocialRefreshToken(providerId, refreshToken);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("provider", "kakao");
            userInfo.put("sub", providerId);
            userInfo.put("name", jsonNode.path("properties").path("nickname").asText());
            userInfo.put("email", jsonNode.path("kakao_account").path("email").asText());
            userInfo.put("picture", jsonNode.path("properties").path("profile_image").asText());

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao user info response", e);
        }
    }

    private String refreshAccessTokenForRefreshToken(String refreshToken){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String requestBody = "grant_type=refresh_token"
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret
                + "&redirect_uri=" + redirectUri
                + "&refresh_token=" + refreshToken;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUri, HttpMethod.POST, request, String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Kakao OAuth token response", e);
        }
    }

    @Override
    public void unlink(String providerId) {
        String socialRefreshToken = socialRefreshTokenService.getRefreshToken(providerId);

        if(socialRefreshToken != null){
            String accessToken = refreshAccessTokenForRefreshToken(socialRefreshToken);
            socialRefreshTokenService.deleteSocialRefreshToken(providerId);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            try{
                HttpEntity<String> request = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate.exchange(unlinkUri, HttpMethod.POST, request, String.class);

                log.info("Kakao unlink success. Response: {}", response.getBody());
            } catch (Exception e) {
                log.error("Kakao unlink failed. providerId: {}, body: {}", providerId, e.getMessage());
                throw new RuntimeException("Kakao unlink failed");
            }
        }
    }

}
