package com.bandhub.zsi.ticketing.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateConcertRequest(
        String name,
        LocalDateTime date,
        String description,
        String imageUrl,
        UUID venueId,
        List<CreateTicketPoolRequest> ticketPools
) {}
