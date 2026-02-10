package com.bandhub.zsi.logistics.domain;

import com.bandhub.zsi.shared.Money; // Importujemy z shared!
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tour_costs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TourCost {

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
    private Money cost;

    @Column(name = "cost_date")
    private LocalDateTime costDate;

    public TourCost(String title, Money cost, LocalDateTime costDate) {
        this.title = title;
        this.cost = cost;
        this.costDate = costDate;
    }
}