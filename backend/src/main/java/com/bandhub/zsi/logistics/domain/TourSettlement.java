package com.bandhub.zsi.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tour_settlements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TourSettlement {

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

    public static TourSettlement create(Tour tour, String settledBy, LocalDateTime settledAt, BigDecimal totalCosts, BigDecimal totalRevenue, BigDecimal balance, String currency, String notes) {
        TourSettlement settlement = new TourSettlement();
        settlement.tour = tour;
        settlement.settledBy = settledBy;
        settlement.settledAt = settledAt;
        settlement.totalCosts = totalCosts;
        settlement.totalRevenue = totalRevenue;
        settlement.balance = balance;
        settlement.currency = currency;
        settlement.notes = notes;
        return settlement;
    }

    public void update(Tour tour, String settledBy, LocalDateTime settledAt, BigDecimal totalCosts, BigDecimal totalRevenue, BigDecimal balance, String currency, String notes) {
        this.tour = tour;
        this.settledBy = settledBy;
        this.settledAt = settledAt;
        this.totalCosts = totalCosts;
        this.totalRevenue = totalRevenue;
        this.balance = balance;
        this.currency = currency;
        this.notes = notes;
    }
}
