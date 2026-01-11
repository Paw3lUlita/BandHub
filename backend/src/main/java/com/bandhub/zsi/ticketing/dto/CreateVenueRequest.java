package com.bandhub.zsi.ticketing.dto;

public record CreateVenueRequest(
        String name,
        String city,
        String street,
        int capacity,
        String contactEmail
) {}
