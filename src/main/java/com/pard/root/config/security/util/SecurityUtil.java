package com.pard.root.config.security.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

@Slf4j
public class SecurityUtil {

    // 현재 로그인한 사용자의 userId 가져오기
    public static UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication: {}", authentication.getPrincipal());

        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new SecurityException("Unauthorized access");
        }

        return UUID.fromString(authentication.getName());
    }

    // 요청된 userId와 현재 로그인한 userId가 같은지 검증
    public static void validateUserAccess(UUID requestedUserId) {
        UUID tokenUserId = getAuthenticatedUserId();
        if (!tokenUserId.equals(requestedUserId)) {
            log.info("Unauthorized access attempt detected! Token User ID: {}, Requested User ID: {}",
                    tokenUserId, requestedUserId);
            throw new SecurityException("Forbidden access");
        }
    }
}
