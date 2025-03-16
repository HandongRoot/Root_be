//package com.pard.root.user.service;
//
//import com.pard.root.auth.blacklist.service.BlacklistedTokenService;
//import com.pard.root.auth.token.repo.TokenRepository;
//import com.pard.root.config.security.service.JwtProvider;
//import com.pard.root.exception.user.UserNotFoundException;
//import com.pard.root.folder.service.CategoryService;
//import com.pard.root.helper.constants.UserState;
//import com.pard.root.user.dto.UserCreateDto;
//import com.pard.root.user.dto.UserReadDto;
//import com.pard.root.user.entity.User;
//import com.pard.root.user.repo.UserRepository;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Map;
//import java.util.Optional;
//import java.util.UUID;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//public class UserServiceImpl implements UserService {
//    User saveUser(Map<String, Object> userInfo);
//	  User findById(UUID id);
//	  UserReadDto findByUserId(UUID id);
//    Optional<User> findByProviderId(String providerId);
//    boolean existsByProviderId(String providerId);
//    void updateUserStateToActive(String providerId);
//    ResponseEntity<String> logout(HttpServletRequest request);
//    ResponseEntity<String> deleteUser(HttpServletRequest request, UUID userId);
//}
