package com.bandhub.zsi.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tour_legs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TourLeg {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @Column(name = "concert_id")
    private UUID concertId;

    @Column(name = "leg_order")
    private int legOrder;

    private String city;

    @Column(name = "venue_name")
    private String venueName;

    @Column(name = "leg_date")
    private LocalDateTime legDate;

    @Column(name = "planned_budget")
    private BigDecimal plannedBudget;

    private String currency;

    public static TourLeg create(Tour tour, UUID concertId, int legOrder, String city, String venueName, LocalDateTime legDate, BigDecimal plannedBudget, String currency) {
        TourLeg leg = new TourLeg();
        leg.tour = tour;
        leg.concertId = concertId;
        leg.legOrder = legOrder;
        leg.city = city;
        leg.venueName = venueName;
        leg.legDate = legDate;
        leg.plannedBudget = plannedBudget;
        leg.currency = currency;
        return leg;
    }

    public void update(Tour tour, UUID concertId, int legOrder, String city, String venueName, LocalDateTime legDate, BigDecimal plannedBudget, String currency) {
        this.tour = tour;
        this.concertId = concertId;
        this.legOrder = legOrder;
        this.city = city;
        this.venueName = venueName;
        this.legDate = legDate;
        this.plannedBudget = plannedBudget;
        this.currency = currency;
    }
}
