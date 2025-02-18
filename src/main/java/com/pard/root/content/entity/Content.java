package com.pard.root.content.entity;

import com.pard.root.content.dto.ContentCreateDto;
import com.pard.root.content.dto.ContentUpdateDto;
import com.pard.root.folder.entity.Category;
import com.pard.root.user.entity.User;
import com.pard.root.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<ContentCategory> contentCategories = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @Column(nullable = false, name = "title")
    private String title;

    @Lob
    @Column(nullable = false, name = "thumbnail", columnDefinition = "TEXT")
    private String thumbnail;

    @Lob
    @Column(nullable = false, name = "linked_url", columnDefinition = "TEXT")
    private String linkedUrl;

    public static Content toEntity(User user, ContentCreateDto dto) {
        return Content.builder()
                .user(user)
                .title(dto.getTitle())
                .thumbnail(dto.getThumbnail())
                .linkedUrl(dto.getLinkedUrl())
                .build();
    }

    public void updateTitle(ContentUpdateDto dto) {
        this.title = dto.getTitle();
    }
}

