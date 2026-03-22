package com.bandhub.zsi.ticketing.dto;

import jakarta.validation.constraints.NotBlank;

public record ScanTicketRequest(
        @NotBlank String codeValue,
        String gateName
) {
}
