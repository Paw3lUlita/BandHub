package com.bandhub.zsi.cms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * Singleton z ustawieniami strony - branding i tresci sterowane z panelu admina.
 * Spelnia wymog projektu: tresci (nazwa zespolu, tagline, hero, about) nie sa
 * hardcodowane w kliencie, tylko edytowalne z poziomu administracji.
 */
@Entity
@Table(name = "site_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SiteSettings {

    public static final short SINGLETON_ID = 1;

    @Id
    private Short id;

    @Column(name = "band_name", nullable = false)
    private String bandName;

    @Column(name = "tagline", length = 500)
    private String tagline;

    @Column(name = "hero_image_url", length = 500)
    private String heroImageUrl;

    @Column(name = "about_text", columnDefinition = "TEXT")
    private String aboutText;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    public static SiteSettings createDefault() {
        SiteSettings settings = new SiteSettings();
        settings.id = SINGLETON_ID;
        settings.bandName = "BandHub";
        settings.updatedAt = LocalDateTime.now();
        return settings;
    }

    public void update(String bandName, String tagline, String heroImageUrl, String aboutText, String updatedBy) {
        Assert.hasText(bandName, "Nazwa zespolu nie moze byc pusta");
        this.bandName = bandName;
        this.tagline = tagline;
        this.heroImageUrl = heroImageUrl;
        this.aboutText = aboutText;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}
