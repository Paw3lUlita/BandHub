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
class TourLeg {

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
}

@Entity
@Table(name = "tour_cost_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TourCostCategory {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private boolean active;
}

@Entity
@Table(name = "tour_revenue_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TourRevenueCategory {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private boolean active;
}

@Entity
@Table(name = "tour_settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class TourSettlement {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false, unique = true)
    private Tour tour;

    @Column(name = "settled_by")
    private String settledBy;

    @Column(name = "settled_at")
    private LocalDateTime settledAt;

    @Column(name = "total_costs")
    private BigDecimal totalCosts;

    @Column(name = "total_revenue")
    private BigDecimal totalRevenue;

    private BigDecimal balance;

    private String currency;

    private String notes;
}
