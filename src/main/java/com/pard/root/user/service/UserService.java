package com.pard.root.user.service;

import com.pard.root.auth.blacklist.service.BlacklistedTokenService;
import com.pard.root.auth.token.repo.TokenRepository;
import com.pard.root.config.security.service.JwtProvider;
import com.pard.root.exception.CustomException;
import com.pard.root.exception.ExceptionCode;
import com.pard.root.helper.constants.UserState;
import com.pard.root.user.dto.UserAccessDto;
import com.pard.root.user.dto.UserAccessResponseDto;
import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.user.dto.UserReadDto;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BlacklistedTokenService blacklistedTokenService;
    private final TokenRepository tokenRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public User saveUser(Map<String, Object> userInfo) {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName((String) userInfo.get("name"));
        userCreateDto.setEmail((String) userInfo.get("email"));
        userCreateDto.setPictureUrl((String) userInfo.get("picture"));
        userCreateDto.setProvider((String) userInfo.get("provider"));
        userCreateDto.setProviderId((String) userInfo.get("sub"));

        Optional<User> existingUser = userRepository.findByProviderId(userCreateDto.getProviderId());
        return existingUser.orElseGet(() -> userRepository.save(User.toEntity(userCreateDto)));
    }

    @Transactional
    public void saveAgrnmt(UUID userId, UserAccessDto userAccessDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        user.setTermsOfServiceAgrmnt(userAccessDto.getTermsOfServiceAgrmnt());
        user.setPrivacyPolicyAgrmnt(userAccessDto.getPrivacyPolicyAgrmnt());
        userRepository.save(user);
    }

    public User findById(UUID userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public UserAccessResponseDto findUserAccess(UUID userId) {
        User user = findById(userId);

        return user != null ? UserAccessResponseDto.from(user) : UserAccessResponseDto.builder().build();
    }

    public UserReadDto findByUserId(UUID id) {
        log.info("Find user by id: {}", id);
        return userRepository.findById(id)
                .map(UserReadDto::new)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }

    public Optional<User> findByProviderId(String providerId) { return userRepository.findByProviderId(providerId); }

    public boolean existsByProviderId(String providerId) {
        return userRepository.existsByProviderId(providerId);
    }

    @Transactional
    public void updateUserStateToActive(String providerId) {
        User user = userRepository.findByProviderId(providerId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
        user.activate();
    }

    public void logout(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveToken(request);

        blacklistedTokenService.addToBlacklist(accessToken);

        String providerId = jwtProvider.parseToken(accessToken).getSubject();
        userRepository.updateUserState(findByProviderId(providerId).orElseThrow().getId(), UserState.DEACTIVATED);
        tokenRepository.deleteByProviderId(providerId);
    }

    @Transactional
    public ResponseEntity<String> deleteUser(HttpServletRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));

        String accessToken = jwtProvider.resolveToken(request);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Access Token is missing");
        }
        String providerId = jwtProvider.parseToken(accessToken).getSubject();
        blacklistedTokenService.addToBlacklist(accessToken);

        user.deactivate();
        tokenRepository.deleteByProviderId(providerId);

        userRepository.save(user);
        return ResponseEntity.ok("User has been successfully deleted and logged out.");
    }
}
