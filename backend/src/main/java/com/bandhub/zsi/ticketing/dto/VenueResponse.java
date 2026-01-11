package com.bandhub.zsi.ticketing.dto;

import java.util.UUID;

public record VenueResponse(
        UUID id,
        String name,
        String city,
        String street,
        int capacity,
        String contactEmail
) {}
