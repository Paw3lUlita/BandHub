package com.bandhub.zsi.ticketing.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConcertResponse(
        UUID id,
        String name,
        LocalDateTime date,
        String venueName,
        String city
) {}
