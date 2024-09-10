package com.pard.root.content.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "category_id")
    private Long categoryId;

    @Column(nullable = false, name = "title")
    private String title;

    @Lob
    @Column(nullable = false, name = "thumbnail", columnDefinition = "TEXT")
    private String pictureUrl;

    @Lob
    @Column(nullable = false, name = "rinked_url", columnDefinition = "TEXT")
    private String rinkedUrl;


}
