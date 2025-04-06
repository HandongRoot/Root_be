package com.pard.root.auth.oauth.service.social;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pard.root.auth.oauth.dto.AppleLoginRequest;
import com.pard.root.auth.token.service.SocialRefreshTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleOauth {

    @Value("${sns.apple.url}")
    private String APPLE_URL;
    @Value("${sns.apple.clientId}")
    private String APPLE_CLIENT_ID;
    @Value("${sns.apple.keyId}")
    private String APPLE_KEY_ID;
    @Value("${sns.apple.teamId}")
    private String APPLE_TEAM_ID;
    @Value("${sns.apple.token.url}")
    private String APPLE_TOKEN_URL;
    @Value("${sns.apple.redirectUri}")
    private String APPLE_REDIRECT_URI;
    @Value("${sns.apple.key.url}")
    private String APPLE_KEY_URL;
    @Value("${sns.apple.privateKeyPath}")
    private String APPLE_PRIVATE_KEY_PATH;
    @Value("${sns.apple.revoke}")
    private String APPLE_REVOKE_URL;
    @Value("${jwt.refresh.token.expiration}")
    private long refreshExpiration;

    private final RestTemplate restTemplate = new RestTemplate();
    private final SocialRefreshTokenService socialRefreshTokenService;

    /**
     * Apple 로그인 처리 및 사용자 정보 반환
     * @param request Apple 로그인 요청 정보
     * @return 사용자 정보 맵
     */
    public Map<String, Object> authenticateWithApple(AppleLoginRequest request) {
        try {
            PublicKey publicKey = fetchApplePublicKey(request.getIdentityToken());
            Claims claims = decodeIdentityToken(request.getIdentityToken(), publicKey);
            Map<String, String> nameMap = request.getFullName();
            boolean isPrivateEmail = Boolean.parseBoolean(claims.get("is_private_email", String.class));
            String email = isPrivateEmail ? request.getUserIdentifier() + "@apple.com" : claims.get("email", String.class);

            Map<String, String> appleTokens = requestAppleTokens(request.getAuthorizationCode());
            log.info("apple tokens: {}", appleTokens);
            socialRefreshTokenService.createSocialRefreshToken(request.getIdentityToken(), appleTokens.get("refresh_token"));


            Map<String, Object> response = new HashMap<>();
            response.put("provider", "apple");
            response.put("sub", request.getUserIdentifier());
            response.put("name", nameMap.get("name"));
            response.put("email", email);
            response.put("picture", null);

            return response;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Apple 공개 키 가져오기
     * @param identityToken Apple ID Token
     * @return 검증된 PublicKey
     */
    private PublicKey fetchApplePublicKey(String identityToken) throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity(APPLE_KEY_URL, String.class);

        String keyId = extractKeyId(identityToken);

        JsonNode keys = new ObjectMapper().readTree(response.getBody()).get("keys");
        for (JsonNode key : keys) {
            if(key.get("kid").asText().equals(keyId)){
                BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(key.get("n").asText()));
                BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(key.get("e").asText()));

                RSAPublicKeySpec spec = new RSAPublicKeySpec(n, e);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(spec);
            }
        }
        throw new IllegalArgumentException("Apple Public Key not found");
    }

    /**
     * Apple ID Token의 Key ID 추출
     * @param identityToken Apple ID Token
     * @return Key ID
     */
    private static String extractKeyId(String identityToken) throws Exception {
        String[] jwtParts = identityToken.split("\\.");
        if (jwtParts.length < 2) {
            throw new IllegalArgumentException("Invalid identity token format");
        }

        String headerJson = new String(Base64.getUrlDecoder().decode(jwtParts[0]));

        JsonNode headerNode = new ObjectMapper().readTree(headerJson);
        return headerNode.get("kid").asText();
    }

    /**
     * Apple ID Token을 디코딩하여 Claims 반환
     * @param identityToken Apple ID Token
     * @param publicKey 검증용 PublicKey
     * @return Claims 객체
     */
    private Claims decodeIdentityToken(String identityToken, PublicKey publicKey) {
        return Jwts.parserBuilder().setSigningKey(publicKey).build().parseClaimsJws(identityToken).getBody();
    }

    /**
     * Apple OAuth 토큰 요청
     * @param authorizationCode Apple Authorization Code
     * @return Apple OAuth 토큰 맵
     */
    public Map<String, String> requestAppleTokens(String authorizationCode) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", APPLE_CLIENT_ID);
        body.add("client_secret", generateClientSecret());
        body.add("code", authorizationCode);
        body.add("grant_type", "authorization_code");
        body.add("redirect_uri", APPLE_REDIRECT_URI);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(
                APPLE_TOKEN_URL,
                HttpMethod.POST,
                request,
                String.class);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.getBody());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", jsonNode.get("access_token").asText());
            tokens.put("refresh_token", jsonNode.get("refresh_token").asText()); // 🔥 여기서 Apple refresh_token 받음
            tokens.put("id_token", jsonNode.get("id_token").asText());

            return tokens;
        } catch (Exception e) {
            throw new RuntimeException("Apple 토큰 요청 실패", e);
        }
    }

    /**
     * Apple Client Secret 생성
     * @return Apple OAuth Client Secret
     */
    public String generateClientSecret() {
        try {
            long now = System.currentTimeMillis();
            long expiresAt = now + refreshExpiration;

            return Jwts.builder()
                    .setHeaderParam("kid", APPLE_KEY_ID)
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(APPLE_TEAM_ID)
                    .setAudience(APPLE_URL)
                    .setSubject(APPLE_CLIENT_ID)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(expiresAt))
                    .signWith(loadPrivateKeyFromFile(), SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            throw new RuntimeException("Apple client_secret 생성 실패", e);
        }
    }

    /**
     * Private Key 파일을 로드하여 PrivateKey 객체로 변환
     * @return PrivateKey 객체
     */
    private PrivateKey loadPrivateKeyFromFile() throws Exception {
        File resource = new File(APPLE_PRIVATE_KEY_PATH);

        try (InputStream is = new FileInputStream(resource);
             Reader reader = new InputStreamReader(is);
             PEMParser pemParser = new PEMParser(reader)) {

            Object obj = pemParser.readObject();
            if (!(obj instanceof PrivateKeyInfo)) {
                throw new IllegalArgumentException("❌ Provided .p8 file is not a valid PrivateKeyInfo object");
            }

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            return converter.getPrivateKey((PrivateKeyInfo) obj);
        }
    }

    /**
     * Apple ID와의 소셜 로그인 연결을 해제하는 메서드.
     * Apple OAuth 2.0의 refresh_token을 사용하여 Apple의 인증 서버에 토큰 철회 요청을 보냅니다.
     * @param providerId 소셜 로그인 제공자 ID
     */
    public void unlink(String providerId) {
        String socialRefreshToken = socialRefreshTokenService.getRefreshToken(providerId);

        if(socialRefreshToken != null){
            socialRefreshTokenService.deleteSocialRefreshToken(providerId);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", APPLE_CLIENT_ID);
            body.add("token", socialRefreshToken);
            body.add("client_secret", generateClientSecret());
            body.add("token_type_hint", "refresh_token");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    APPLE_REVOKE_URL,
                    HttpMethod.POST,
                    request,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("✅ 200: Apple ID Unlink Success!");
                System.out.println("Response Body: " + response.getBody());
            } else {
                System.err.println("❌ 400: Apple ID Unlink Failed!");
                System.err.println("Response Body: " + response.getBody());
            }
        } else {
            System.err.println("⚠ No refresh token found for provider: " + providerId);
        }
    }
}
