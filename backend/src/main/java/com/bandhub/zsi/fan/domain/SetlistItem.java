package com.bandhub.zsi.fan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "setlist_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SetlistItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "setlist_id", nullable = false)
    private Setlist setlist;

    @Column(name = "song_title")
    private String songTitle;

    @Column(name = "song_order")
    private int songOrder;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    public static SetlistItem create(Setlist setlist, String songTitle, int songOrder, Integer durationSeconds) {
        SetlistItem item = new SetlistItem();
        item.setlist = setlist;
        item.songTitle = songTitle;
        item.songOrder = songOrder;
        item.durationSeconds = durationSeconds;
        return item;
    }

    public void update(Setlist setlist, String songTitle, int songOrder, Integer durationSeconds) {
        this.setlist = setlist;
        this.songTitle = songTitle;
        this.songOrder = songOrder;
        this.durationSeconds = durationSeconds;
    }
}
