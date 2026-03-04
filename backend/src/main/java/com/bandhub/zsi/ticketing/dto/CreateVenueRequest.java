package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVenueRequest(
        @NotBlank(message = "Venue name is required")
        @Size(max = 255, message = "Venue name cannot exceed 255 characters")
        String name,

        @NotBlank(message = "City is required")
        @Size(max = 100, message = "City cannot exceed 100 characters")
        String city,

        @Size(max = 255, message = "Street cannot exceed 255 characters")
        String street,

        @Min(value = 1, message = "Capacity must be greater than zero")
        int capacity,

        @Email(message = "Contact email must be valid")
        @Size(max = 255, message = "Contact email cannot exceed 255 characters")
        String contactEmail
) {}
