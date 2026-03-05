package com.bandhub.zsi.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Map;
import java.util.UUID;

public record PlaceOrderCommand(
        @NotEmpty(message = "Order must contain at least one item")
        Map<@NotNull UUID, @NotNull @Min(value = 1, message = "Quantity must be at least 1") Integer> items,

        @Size(max = 500)
        String deliveryAddress,

        String paymentProvider
) {}
