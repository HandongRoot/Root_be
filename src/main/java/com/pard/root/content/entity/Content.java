package com.pard.root.content.entity;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, name = "title")
    private String title;

    @Lob
    @Column(nullable = false, name = "thumbnail", columnDefinition = "TEXT")
    private String pictureUrl;

    @Lob
    @Column(nullable = false, name = "linked_url", columnDefinition = "TEXT")
    private String linkedUrl;

    public static Content toEntity(Category category, User user, ContentCreateDto dto) {
        return Content.builder()
                .category(category)
                .user(user)
                .title(dto.getTitle())
                .pictureUrl(dto.getPictureUrl())
                .linkedUrl(dto.getLinkedUrl())
                .build();
    }

    public void changeCategory(Category category) {
        this.category = category;
    }

    public void changeUser(User user) {
        this.user = user;
    }
}

