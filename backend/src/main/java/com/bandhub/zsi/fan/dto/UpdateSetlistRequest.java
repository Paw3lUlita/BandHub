package com.bandhub.zsi.fan.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public record UpdateSetlistRequest(
        @NotBlank String title,
        LocalDateTime publishedAt
) {}
