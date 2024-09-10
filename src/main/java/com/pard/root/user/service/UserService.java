package com.pard.root.user.service;

import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.user.dto.UserReadDto;
import com.pard.root.user.entity.User;
import com.pard.root.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserCreateDto dto){
        User user = userRepository.save(User.toEntity(dto));
    }
}
