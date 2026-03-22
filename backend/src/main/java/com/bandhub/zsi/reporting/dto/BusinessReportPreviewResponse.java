package com.bandhub.zsi.reporting.dto;

/**
 * Odpowiedź podglądu JSON dla generatora raportów — payload to istniejące DTO modułów domenowych.
 */
public record BusinessReportPreviewResponse(BusinessReportType reportType, Object payload) {
}
