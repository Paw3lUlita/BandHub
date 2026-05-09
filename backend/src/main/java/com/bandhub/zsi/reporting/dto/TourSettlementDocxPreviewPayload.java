package com.bandhub.zsi.reporting.dto;

import com.bandhub.zsi.logistics.dto.TourProfitabilityResponse;
import com.bandhub.zsi.logistics.dto.TourSettlementResponse;

import java.util.UUID;

/**
 * Podgląd JSON dla wydruku DOCX rozliczenia trasy — dane do obrony i weryfikacji placeholderów.
 */
public record TourSettlementDocxPreviewPayload(
        boolean settlementPresent,
        TourSettlementResponse settlement,
        TourProfitabilityResponse profitability,
        UUID activeTemplateId,
        String activeTemplateName
) {
}
