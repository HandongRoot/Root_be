package com.pard.root.folder.entity;

import com.pard.root.folder.dto.CategoryCreateDto;
import com.pard.root.user.entity.User;
import com.pard.root.utility.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false , name= "category_name")
    private String title;

    @Column(name = "count_contents")
    private Integer countContents;

    public static Category toEntity(User user, String title, Integer countContents) {
        return Category.builder()
                .user(user)
                .title(title)
                .countContents(countContents)
                .build();

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
