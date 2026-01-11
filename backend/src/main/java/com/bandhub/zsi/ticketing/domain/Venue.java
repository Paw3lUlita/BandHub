package com.bandhub.zsi.ticketing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import java.util.UUID;

@Entity
@Table(name = "venues")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Venue {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String city;
    private String street;
    private int capacity;

    @Column(name = "contact_email")
    private String contactEmail;

    // Factory Method
    public static Venue create(String name, String city, String street, int capacity, String contactEmail) {
        Venue venue = new Venue();
        venue.updateDetails(name, city, street, capacity, contactEmail);
        return venue;
    }

    public void updateDetails(String name, String city, String street, int capacity, String contactEmail) {
        Assert.hasText(name, "Venue name cannot be empty");
        Assert.hasText(city, "City cannot be empty");
        Assert.isTrue(capacity > 0, "Capacity must be positive");

        this.name = name;
        this.city = city;
        this.street = street;
        this.capacity = capacity;
        this.contactEmail = contactEmail;
    }
}
