package com.pard.root.folder.dto;


import com.pard.root.folder.entity.Category;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryReadDto {
    private Long id;
    private String title;
    private Long countContents;

    public Long numberContents(Category category) {
        this.countContents = category.getCountContents();
        return countContents;
    }

    public CategoryReadDto(Category category) {
        this.id = category.getId();
        this.title = category.getTitle();
        this.countContents = category.getCountContents();
    }
}
