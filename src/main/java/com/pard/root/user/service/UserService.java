package com.pard.root.user.service;

import com.pard.root.config.security.service.JwtProvider;
import com.pard.root.auth.blacklist.service.BlacklistedTokenService;
import com.pard.root.auth.token.service.TokenService;
import com.pard.root.helper.constants.UserState;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BlacklistedTokenService blacklistedTokenService;
    private final TokenService tokenService;
    private final JwtProvider jwtProvider;

    @Transactional
    public String saveUser(Map<String, Object> userInfo) {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName((String) userInfo.get("name"));
        userCreateDto.setEmail((String) userInfo.get("email"));
        userCreateDto.setPictureUrl((String) userInfo.get("picture"));
        userCreateDto.setProvider((String) userInfo.get("provider"));
        userCreateDto.setProviderId((String) userInfo.get("sub"));

        Optional<User> existingUser = userRepository.findByProviderId(userCreateDto.getProviderId());
        existingUser.orElseGet(() -> userRepository.save(User.toEntity(userCreateDto)));
        userRepository.flush();

        return existingUser.map(User::getProviderId).orElse(null);
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public UserReadDto findByUserId(UUID id) {
        return new UserReadDto(Objects.requireNonNull(userRepository.findById(id).orElse(null)));
    }

    public Optional<User> findByProviderId(String providerId) { return userRepository.findByProviderId(providerId); }

    public void createUser(UserCreateDto dto){
        User user = userRepository.save(User.toEntity(dto));
    }

    public boolean existsByProviderId(String providerId) {
        return userRepository.existsByProviderId(providerId);
    }

    public ResponseEntity<String> logout(HttpServletRequest request) {
        String accessToken = jwtProvider.resolveToken(request);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Access Token is missing");
        }
        blacklistedTokenService.addToBlacklist(accessToken);

        String providerId = jwtProvider.parseToken(accessToken).getSubject();
        tokenService.deleteByProviderId(providerId);

        return ResponseEntity.ok("Logout successful");
    }

    @Transactional
    public ResponseEntity<String> deleteUser(HttpServletRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User does not exist."));

        String accessToken = jwtProvider.resolveToken(request);
        if (accessToken == null) {
            return ResponseEntity.badRequest().body("Access Token is missing");
        }
        blacklistedTokenService.addToBlacklist(accessToken);

        userRepository.updateUserState(user.getId(), UserState.DEACTIVATED);

        String providerId = jwtProvider.parseToken(accessToken).getSubject();
        tokenService.deleteByProviderId(providerId);


        return ResponseEntity.ok("User has been successfully deleted and logged out.");
    }
}
