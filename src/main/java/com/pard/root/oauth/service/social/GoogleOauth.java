package com.pard.root.oauth.service.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pard.root.token.component.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOauth implements SocialOauth {
    @Value("${sns.google.url}")
    private String GOOGLE_SNS_BASE_URL;
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_CALLBACK_URL;
    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;
    @Value("${sns.google.token.url}")
    private String GOOGLE_SNS_TOKEN_BASE_URL;
    @Value("${sns.google.userInfo}")
    private String GOOGLE_USER_INFO_BASE_URL;
    @Value("${spring.security.oauth2.client.registration.google.client-name}")
    private String GOOGLE_CLIENT_NAME;


    @Override
    public String getOauthRedirectURL() {
        Map<String, Object> params = new HashMap<>();
        params.put("scope", "profile email");
        params.put("response_type", "code");
        params.put("client_id", GOOGLE_CLIENT_ID);
        params.put("redirect_uri", GOOGLE_CALLBACK_URL);
        params.put("client_name", GOOGLE_CLIENT_NAME);
        params.putAll(buildCommonOAuthParams());

        String parameterString = params.entrySet().stream()
                .map(x -> x.getKey() + "=" + x.getValue())
                .collect(Collectors.joining("&"));

        return GOOGLE_SNS_BASE_URL + "?" + parameterString;
    }

    @Override
    public Map<String, Object> requestAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String, Object> params = new HashMap<>();
        params.put("code", code);
        params.put("client_id", GOOGLE_CLIENT_ID);
        params.put("client_secret", GOOGLE_CLIENT_SECRET);
        params.put("redirect_uri", GOOGLE_CALLBACK_URL);
        params.put("grant_type", "authorization_code");
        params.putAll(buildCommonOAuthParams());

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(GOOGLE_SNS_TOKEN_BASE_URL, params, String.class);
            if (responseEntity.getStatusCode() == HttpStatus.OK) {
                String body = responseEntity.getBody();
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> responseMap = objectMapper.readValue(body, Map.class);

                String accessToken = (String) responseMap.get("access_token");
                String refreshToken = (String) responseMap.get("refresh_token");

                Map<String, Object> result = new HashMap<>();
                result.put("access_token", accessToken);
                result.put("refresh_token", refreshToken);

                return result;
            }
        } catch (Exception e) {
            log.error("Google OAuth Error: {}", e.getMessage());
            throw new RuntimeException("구글 로그인 요청 처리 실패");
        }

        throw new RuntimeException("구글 로그인 요청 처리 실패");
    }

    @Override
    public Map<String, Object> getUserInfo(Map<String, Object> token) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.get("access_token"));

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                GOOGLE_USER_INFO_BASE_URL,
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("provider", "google");
            userInfo.put("sub", jsonNode.get("sub").asText());
            userInfo.put("name", jsonNode.get("name").asText());
            userInfo.put("email", jsonNode.get("email").asText());
            userInfo.put("picture", jsonNode.get("picture").asText());

            return userInfo;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Google user info response", e);
        }
    }

    private Map<String, Object> buildCommonOAuthParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("access_type", "offline");
        params.put("prompt", "consent");
        return params;
    }
}