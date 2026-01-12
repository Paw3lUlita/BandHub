package com.bandhub.zsi.cms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "gallery_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GalleryImage {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    public static GalleryImage upload(String title, String filename) {
        GalleryImage img = new GalleryImage();
        img.title = title;
        img.imageUrl = filename;
        img.uploadedAt = LocalDateTime.now();
        return img;
    }
}
