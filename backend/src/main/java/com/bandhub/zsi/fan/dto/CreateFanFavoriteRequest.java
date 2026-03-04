package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateFanFavoriteRequest(
        @NotBlank String fanId,
        @NotBlank String favoriteType,
        @NotNull UUID referenceId
) {}
