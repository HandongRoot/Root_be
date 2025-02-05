package com.pard.root.content.dto;


import com.pard.root.content.entity.Content;
import com.pard.root.folder.dto.CategoryReadDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ContentReadDto {
    private Long id;
    private String title;
    private String thumbnail;
    private String linkedUrl;
    private LocalDateTime createdDate;
    private CategoryReadDto category;

    public ContentReadDto(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.thumbnail = content.getThumbnail();
        this.linkedUrl = content.getLinkedUrl();
        this.createdDate = content.getCreatedDate();
    }

    public ContentReadDto(Content content, CategoryReadDto category) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.thumbnail = content.getThumbnail();
        this.linkedUrl = content.getLinkedUrl();
        this.category = category;
    }
}
