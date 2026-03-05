package com.bandhub.zsi.ecommerce;

import com.bandhub.zsi.ecommerce.dto.MerchSalesSnapshotResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin/reports/merch")
@PreAuthorize("hasRole('ADMIN')")
class MerchReportController {

    private final MerchReportService service;

    MerchReportController(MerchReportService service) {
        this.service = service;
    }

    @GetMapping("/sales-snapshot")
    ResponseEntity<MerchSalesSnapshotResponse> getSalesSnapshot(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.atTime(23, 59, 59) : null;
        return ResponseEntity.ok(service.getMerchSalesSnapshot(fromDt, toDt));
    }
}
