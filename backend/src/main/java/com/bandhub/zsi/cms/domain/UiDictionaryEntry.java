package com.bandhub.zsi.cms.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.time.LocalDateTime;

/**
 * Mikro-copywriting: pojedynczy wpis slownika UI (klucz -> wartosc).
 * Klient (mobile fan app, admin web) pobiera plaska mape przy starcie.
 * Klucz uzywany w kodzie kliencie, wartosc edytowalna z panelu admina.
 */
@Entity
@Table(name = "ui_dictionary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UiDictionaryEntry {

    @Id
    @Column(name = "key_name", length = 150)
    private String key;

    @Column(name = "value", nullable = false, columnDefinition = "TEXT")
    private String value;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private String updatedBy;

    public static UiDictionaryEntry create(String key, String value, String description, String updatedBy) {
        Assert.hasText(key, "Klucz nie moze byc pusty");
        Assert.hasText(value, "Wartosc nie moze byc pusta");
        UiDictionaryEntry entry = new UiDictionaryEntry();
        entry.key = key;
        entry.value = value;
        entry.description = description;
        entry.updatedAt = LocalDateTime.now();
        entry.updatedBy = updatedBy;
        return entry;
    }

    public void update(String value, String description, String updatedBy) {
        Assert.hasText(value, "Wartosc nie moze byc pusta");
        this.value = value;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
        this.updatedBy = updatedBy;
    }
}
