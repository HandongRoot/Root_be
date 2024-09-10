package com.pard.root.user.controller;

import com.pard.root.user.dto.UserCreateDto;
import com.pard.root.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public void createUser(@RequestBody UserCreateDto dto){
        userService.createUser(dto);
    }


}
