package com.bandhub.zsi.cms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "news_articles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NewsArticle {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "published_date")
    private LocalDateTime publishedDate;

    @Column(name = "author_id")
    private String authorId;

    public static NewsArticle publish(String title, String content, String imageUrl, String authorId) {
        NewsArticle news = new NewsArticle();
        news.id = UUID.randomUUID();
        news.publishedDate = LocalDateTime.now();
        news.authorId = authorId;
        news.updateContent(title, content, imageUrl);
        return news;
    }

    public void updateContent(String title, String content, String imageUrl) {
        Assert.hasText(title, "Title cannot be empty");
        Assert.hasText(content, "Content cannot be empty");
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
    }
}
