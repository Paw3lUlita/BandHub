package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PlaceTicketOrderCommand(
        @NotNull UUID concertId,
        @NotEmpty(message = "Order must contain at least one pool line")
        Map<@NotNull UUID, @NotNull @Min(value = 1, message = "Quantity must be at least 1") Integer> items
) {
}
