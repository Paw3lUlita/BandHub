package com.bandhub.zsi.logistics.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "tour_cost_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TourCostCategory {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private boolean active;

    public static TourCostCategory create(String code, String name, boolean active) {
        TourCostCategory category = new TourCostCategory();
        category.code = code;
        category.name = name;
        category.active = active;
        return category;
    }

    public void update(String code, String name, boolean active) {
        this.code = code;
        this.name = name;
        this.active = active;
    }
}
