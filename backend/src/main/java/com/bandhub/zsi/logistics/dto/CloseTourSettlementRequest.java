package com.bandhub.zsi.logistics.dto;

/**
 * Optional notes appended after DB recomputes totals ({@code fn_close_tour_settlement}).
 */
public record CloseTourSettlementRequest(String notes) {}
