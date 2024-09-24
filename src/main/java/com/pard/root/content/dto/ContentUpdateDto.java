package com.pard.root.content.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Getter
@Setter
public class ContentUpdateDto {
    private Long id;
    private String title;
    private String picture;
    private String linkedUrl;
}
