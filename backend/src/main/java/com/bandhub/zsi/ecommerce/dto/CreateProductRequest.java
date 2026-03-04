package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name cannot exceed 255 characters")
        String name,

        @Size(max = 2000, message = "Description cannot exceed 2000 characters")
        String description,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @NotBlank(message = "Currency is required")
        @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must use ISO 4217 format, e.g. PLN")
        String currency,

        @Min(value = 0, message = "Stock quantity cannot be negative")
        int stockQuantity,

        @NotNull(message = "Category is required")
        UUID categoryId
) {}