package com.bandhub.zsi.logistics.domain;

import com.bandhub.zsi.shared.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tour_revenues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TourRevenue {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "currency"))
    })
    private Money revenue;

    @Column(name = "revenue_date")
    private LocalDateTime revenueDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "revenue_category_id")
    private TourRevenueCategory revenueCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_leg_id")
    private TourLeg tourLeg;

    public TourRevenue(String title, Money revenue, LocalDateTime revenueDate, TourRevenueCategory revenueCategory, TourLeg tourLeg) {
        this.title = title;
        this.revenue = revenue;
        this.revenueDate = revenueDate;
        this.revenueCategory = revenueCategory;
        this.tourLeg = tourLeg;
    }

    public void update(String title, Money revenue, LocalDateTime revenueDate, TourRevenueCategory revenueCategory, TourLeg tourLeg) {
        this.title = title;
        this.revenue = revenue;
        this.revenueDate = revenueDate;
        this.revenueCategory = revenueCategory;
        this.tourLeg = tourLeg;
    }
}
