package com.bandhub.zsi.cms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUiDictionaryEntryRequest(
        @NotBlank(message = "Klucz jest wymagany")
        @Size(max = 150)
        String key,

        @NotBlank(message = "Wartosc jest wymagana")
        String value,

        @Size(max = 255)
        String description
) {}
