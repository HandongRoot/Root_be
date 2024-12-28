package com.pard.root.user.service;

import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveUser(Map<String, Object> userInfo) {
        UserCreateDto userCreateDto = new UserCreateDto();
        userCreateDto.setName((String) userInfo.get("name"));
        userCreateDto.setEmail((String) userInfo.get("email"));
        userCreateDto.setPictureUrl((String) userInfo.get("picture"));
        userCreateDto.setProvider((String) userInfo.get("provider"));
        userCreateDto.setProviderId((String) userInfo.get("sub"));

        Optional<User> existingUser = userRepository.findByEmail(userCreateDto.getEmail());
        existingUser.orElseGet(() -> userRepository.save(User.toEntity(userCreateDto)));
    }

    public User findById(UUID id) {
        return userRepository.findById(id).orElse(null);
    }

    public void createUser(UserCreateDto dto){
        User user = userRepository.save(User.toEntity(dto));
    }
}
