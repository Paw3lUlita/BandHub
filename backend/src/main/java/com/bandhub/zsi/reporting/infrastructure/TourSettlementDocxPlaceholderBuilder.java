package com.bandhub.zsi.reporting.infrastructure;

import com.bandhub.zsi.logistics.dto.TourProfitabilityResponse;
import com.bandhub.zsi.logistics.dto.TourSettlementResponse;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Mapa placeholderów dla szablonu Word — klucze w formacie {@code ${nazwa}}.
 * Publiczna, bo wywoływana z {@link com.bandhub.zsi.reporting.BusinessReportService} (inny pakiet niż {@code infrastructure}).
 */
public final class TourSettlementDocxPlaceholderBuilder {

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TourSettlementDocxPlaceholderBuilder() {
    }

    public static Map<String, String> build(Optional<TourSettlementResponse> settlement, TourProfitabilityResponse profit) {
        Map<String, String> m = new LinkedHashMap<>();
        settlement.ifPresentOrElse(
                s -> {
                    m.put("${tourId}", nullToEmpty(s.tourId() != null ? s.tourId().toString() : ""));
                    m.put("${tourName}", nullToEmpty(s.tourName()));
                    m.put("${settledBy}", nullToEmpty(s.settledBy()));
                    m.put("${settledAt}", s.settledAt() != null ? DT.format(s.settledAt()) : "");
                    m.put("${settlementTotalCosts}", s.totalCosts() != null ? s.totalCosts().toPlainString() : "");
                    m.put("${settlementTotalRevenue}", s.totalRevenue() != null ? s.totalRevenue().toPlainString() : "");
                    m.put("${settlementBalance}", s.balance() != null ? s.balance().toPlainString() : "");
                    m.put("${settlementCurrency}", nullToEmpty(s.currency()));
                    m.put("${settlementNotes}", nullToEmpty(s.notes()));
                },
                () -> {
                    m.put("${tourId}", "");
                    m.put("${tourName}", "");
                    m.put("${settledBy}", "");
                    m.put("${settledAt}", "");
                    m.put("${settlementTotalCosts}", "");
                    m.put("${settlementTotalRevenue}", "");
                    m.put("${settlementBalance}", "");
                    m.put("${settlementCurrency}", "");
                    m.put("${settlementNotes}", "");
                }
        );
        m.put("${profitTotalCosts}", profit.totalCosts() != null ? profit.totalCosts().toPlainString() : "");
        m.put("${ticketRevenue}", profit.ticketRevenue() != null ? profit.ticketRevenue().toPlainString() : "");
        m.put("${manualRevenue}", profit.manualRevenue() != null ? profit.manualRevenue().toPlainString() : "");
        m.put("${profitTotalRevenue}", profit.totalRevenue() != null ? profit.totalRevenue().toPlainString() : "");
        m.put("${profitBalance}", profit.balance() != null ? profit.balance().toPlainString() : "");
        m.put("${profitCurrency}", nullToEmpty(profit.currency()));
        return m;
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }
}
