package com.bandhub.zsi.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUiDictionaryEntryRequest(
        @NotBlank(message = "Wartosc jest wymagana")
        String value,

        @Size(max = 255)
        String description
) {}
