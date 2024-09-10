package com.pard.root.folder.dto;

import com.pard.root.user.entity.User;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CategoryCreateDto {
    private User user;
    private String name;
}
