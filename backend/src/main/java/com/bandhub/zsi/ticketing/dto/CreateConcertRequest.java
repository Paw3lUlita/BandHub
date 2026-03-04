package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateConcertRequest(
        @NotBlank(message = "Concert name is required")
        @Size(max = 255, message = "Concert name cannot exceed 255 characters")
        String name,

        @NotNull(message = "Concert date is required")
        LocalDateTime date,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        String imageUrl,

        @NotNull(message = "Venue is required")
        UUID venueId,

        @NotEmpty(message = "At least one ticket pool is required")
        List<@Valid CreateTicketPoolRequest> ticketPools
) {}
