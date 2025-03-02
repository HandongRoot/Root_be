package com.pard.root.content.dto;

import com.pard.root.content.entity.Content;
import com.pard.root.folder.dto.CategoryReadDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
public class ContentWithListCategoryReadDto {
    private Long id;
    private String title;
    private String thumbnail;
    private String linkedUrl;
    private LocalDateTime createdDate;
    private List<CategoryReadDto> categories;

    public ContentWithListCategoryReadDto(Content content, List<CategoryReadDto> categories) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.thumbnail = content.getThumbnail();
        this.linkedUrl = content.getLinkedUrl();
        this.categories = categories;
    }
}
