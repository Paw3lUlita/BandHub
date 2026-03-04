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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "tour_id")
    private List<TourRevenue> revenues = new ArrayList<>();

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

    public void updateDetails(String name, String description, LocalDateTime startDate, LocalDateTime endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public void logCost(TourCost cost) {
        this.costs.add(cost);
    }

    public TourCost getCost(UUID costId) {
        return this.costs.stream()
                .filter(cost -> cost.getId().equals(costId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cost not found: " + costId));
    }

    public void removeCost(UUID costId) {
        boolean removed = this.costs.removeIf(cost -> cost.getId().equals(costId));
        if (!removed) {
            throw new IllegalArgumentException("Cost not found: " + costId);
        }
    }

    public void logRevenue(TourRevenue revenue) {
        this.revenues.add(revenue);
    }

    public TourRevenue getRevenue(UUID revenueId) {
        return this.revenues.stream()
                .filter(revenue -> revenue.getId().equals(revenueId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Revenue not found: " + revenueId));
    }

    public void removeRevenue(UUID revenueId) {
        boolean removed = this.revenues.removeIf(revenue -> revenue.getId().equals(revenueId));
        if (!removed) {
            throw new IllegalArgumentException("Revenue not found: " + revenueId);
        }
    }
}
