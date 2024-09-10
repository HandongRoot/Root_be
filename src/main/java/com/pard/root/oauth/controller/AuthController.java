package com.pard.root.oauth.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/oauth")
public class AuthController {

//    @PostMapping("/googleLogin")
//    @Operation(summary = "구글 로그인", description = "구글 로그인 후 이메일 반환")
//    public Map<String, Object> googleLogin(@RequestBody Map<String, Object> userData, HttpServletResponse response) {
//        String idTokenString = (String) userData.get("token");
//
//        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
//                .setAudience(Collections.singletonList(googleClientId))
//                .build();
//
//        GoogleIdToken idToken;
//        try {
//            idToken = verifier.verify(idTokenString);
//        } catch (GeneralSecurityException | IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        if (idToken != null) {
//            GoogleIdToken.Payload payload = idToken.getPayload();
////            String email = payload.getEmail();
////            String name = (String) payload.get("name");
////            String picture = (String) payload.get("picture");
//
//            Map<String, Object> userInfo = userService.saveOrUpdateUser(payload);
//
////            String accessToken = jwtService.generateAccessToken(email);
////            String refreshToken = authService.createRefreshToken(email);
//
////            setCookie(response, "access_token", accessToken, (int) (jwtService.getAccessTokenExpiration() / 1000));
////            setCookie(response, "refresh_token", refreshToken, (int) (jwtService.getRefreshTokenExpiration() / 1000));
////            log.info("\uD83D\uDCCD gmail login");
////            userInfo.put("access_token", accessToken);
////            userInfo.put("refresh_token", refreshToken);
//            return userInfo;
//        } else {
//            throw new RuntimeException("Invalid ID token");
//        }
//    }
}
