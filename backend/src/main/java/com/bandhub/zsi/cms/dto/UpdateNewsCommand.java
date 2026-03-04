package com.bandhub.zsi.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateNewsCommand(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title cannot exceed 255 characters")
        String title,

        @NotBlank(message = "Content is required")
        String content,

        @Size(max = 500, message = "Image URL cannot exceed 500 characters")
        String imageUrl
) {}
