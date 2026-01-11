package com.bandhub.zsi.ticketing.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ConcertDetailResponse(
        UUID id,
        String name,
        LocalDateTime date,
        String description,
        String imageUrl,
        String venueName,
        String venueCity,
        List<TicketPoolResponse> ticketPools
) {}