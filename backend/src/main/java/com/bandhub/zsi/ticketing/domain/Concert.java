package com.bandhub.zsi.ticketing.domain;

import com.bandhub.zsi.ecommerce.domain.Money;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "concerts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Concert {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private LocalDateTime date;

    @Column(length = 2000)
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    // Ignorujemy na razie tour_name z bazy, wrócimy do tego przy Logistyce
    // private String tourName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id") // To pasuje do V1
    private Venue venue;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "concert_id")
    private List<TicketPool> ticketPools = new ArrayList<>();

    public static Concert plan(String name, LocalDateTime date, Venue venue) {
        Concert concert = new Concert();
        concert.name = name;
        concert.date = date;
        concert.venue = venue;
        return concert;
    }

    public void updateDetails(String name, LocalDateTime date, String description, String imageUrl, Venue venue) {
        this.name = name;
        this.date = date;
        this.description = description;
        this.imageUrl = imageUrl;
        this.venue = venue;
    }

    public void addTicketPool(TicketPool pool) {
        this.ticketPools.add(pool);
    }

    // Importuj Money z modułu ecommerce
    // import com.bandhub.zsi.ecommerce.domain.Money;
    // import java.math.BigDecimal;

    public void configureTicketPool(String poolName, BigDecimal price, String currency, int quantity) {
        Money money = new Money(price, currency);
        TicketPool pool = new TicketPool(poolName, money, quantity);
        this.ticketPools.add(pool);
    }
}