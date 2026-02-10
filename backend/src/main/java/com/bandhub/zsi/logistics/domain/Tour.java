package com.bandhub.zsi.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "tours")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tour {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tour_id")
    private List<TourCost> costs = new ArrayList<>();

    public static Tour plan(String name, LocalDateTime startDate, LocalDateTime endDate) {
        Tour tour = new Tour();
        tour.name = name;
        tour.startDate = startDate;
        tour.endDate = endDate;
        return tour;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void logCost(TourCost cost) {
        this.costs.add(cost);
    }
}
