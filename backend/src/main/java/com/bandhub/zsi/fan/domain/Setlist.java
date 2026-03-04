package com.bandhub.zsi.fan.domain;

import com.bandhub.zsi.ticketing.domain.Concert;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "setlists")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Setlist {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    private String title;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public static Setlist create(Concert concert, String title, String createdBy, LocalDateTime publishedAt) {
        Setlist setlist = new Setlist();
        setlist.id = UUID.randomUUID();
        setlist.concert = concert;
        setlist.title = title;
        setlist.createdBy = createdBy;
        setlist.publishedAt = publishedAt;
        setlist.createdAt = LocalDateTime.now();
        return setlist;
    }

    public void update(String title, LocalDateTime publishedAt) {
        this.title = title;
        this.publishedAt = publishedAt;
    }
}
