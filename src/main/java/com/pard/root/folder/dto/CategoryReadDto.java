package com.pard.root.folder.dto;


import com.pard.root.content.dto.ContentReadDto;
import com.pard.root.content.entity.Content;
import com.pard.root.folder.entity.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CategoryReadDto {
    private Long id;
    private String title;
    private Integer countContents;
    private List<ContentReadDto> contentReadDtos;

    public Integer numberContents(Category category) {
        this.countContents = category.getCountContents();
        return countContents;
    }

    public CategoryReadDto(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.countContents = category.getCountContents();
    }

    public CategoryReadDto(Category category, List<ContentReadDto> contentReadDtos) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.countContents = category.getCountContents();
        this.contentReadDtos = contentReadDtos;
    }
}
