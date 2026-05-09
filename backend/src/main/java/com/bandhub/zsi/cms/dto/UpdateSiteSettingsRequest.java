package com.bandhub.zsi.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateSiteSettingsRequest(
        @NotBlank(message = "Nazwa zespolu jest wymagana")
        @Size(max = 255)
        String bandName,

        @Size(max = 500)
        String tagline,

        @Size(max = 500)
        String heroImageUrl,

        String aboutText
) {}
