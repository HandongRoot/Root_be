package com.pard.root.user.service;

import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.user.dto.UserReadDto;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
        User user = userRepository.findById(id).orElse(null);
        return user;
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
}
