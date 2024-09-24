package com.pard.root.content.dto;


import com.pard.root.content.entity.Content;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContentReadDto {
    private Long id;
    private String title;
    private String pictureUrl;
    private String linkedUrl;

    public ContentReadDto(Content content) {
        this.id = content.getId();
        this.title = content.getTitle();
        this.pictureUrl = content.getPictureUrl();
        this.linkedUrl = content.getLinkedUrl();
    }
}
