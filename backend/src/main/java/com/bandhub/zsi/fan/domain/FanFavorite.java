package com.bandhub.zsi.fan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fan_favorites")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FanFavorite {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "fan_id")
    private String fanId;

    @Column(name = "favorite_type")
    private String favoriteType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static FanFavorite create(String fanId, String favoriteType, UUID referenceId) {
        FanFavorite favorite = new FanFavorite();
        favorite.fanId = fanId;
        favorite.favoriteType = favoriteType;
        favorite.referenceId = referenceId;
        favorite.createdAt = LocalDateTime.now();
        return favorite;
    }

    public void update(String fanId, String favoriteType, UUID referenceId) {
        this.fanId = fanId;
        this.favoriteType = favoriteType;
        this.referenceId = referenceId;
    }
}
