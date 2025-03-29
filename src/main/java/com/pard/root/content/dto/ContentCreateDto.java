package com.pard.root.content.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ContentCreateDto {
    private String title;
    private String thumbnail;
    private String linkedUrl;
}
