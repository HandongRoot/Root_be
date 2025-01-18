package com.pard.root.folder.entity;

import com.pard.root.content.entity.Content;
import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.folder.dto.CategoryUpdateDto;
import com.pard.root.user.entity.User;
import com.pard.root.utility.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "categorys")
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Content> Contents = new ArrayList<>();

    @Column(nullable = false , name= "category_name")
    private String title;

    @Column(name = "count_contents")
    private Integer countContents;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Content> contents;

    public static Category toEntity(User user, String title, Integer countContents) {
        return Category.builder()
                .user(user)
                .title(title)
                .countContents(countContents)
                .build();

    }
    public void updateTitle(CategoryUpdateDto updateDto) {
        this.title = updateDto.getTitle();
    }

    public void incrementCountContents() {
        if (this.countContents == null) {
            this.countContents = 0;
        }
        this.countContents++;
    }

    public void decrementCountContents() {
        if (this.countContents == null || this.countContents <= 0) {
            this.countContents = 0;
        }
        this.countContents--;
    }
}
