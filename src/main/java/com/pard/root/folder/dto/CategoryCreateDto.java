package com.pard.root.folder.dto;

import com.pard.root.user.entity.User;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class CategoryCreateDto {
    private UUID userId;
    private String title;
}
