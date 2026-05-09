package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.dto.BusinessReportPreviewResponse;
import com.bandhub.zsi.reporting.dto.BusinessReportType;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

/**
 * REST: generator raportów biznesowych (podgląd + eksport PDF/XLSX/DOCX) z audytem w {@code report_runs} / {@code export_jobs}.
 */
@RestController
@RequestMapping("/api/admin/reports/business")
@PreAuthorize("hasRole('ADMIN')")
class BusinessReportsController {

    private final BusinessReportService businessReportService;

    BusinessReportsController(BusinessReportService businessReportService) {
        this.businessReportService = businessReportService;
    }

    /**
     * Podgląd danych raportu (JSON) + zapis rekordu uruchomienia w {@code report_runs}.
     */
    @GetMapping(value = "/preview", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BusinessReportPreviewResponse> preview(
            @RequestParam BusinessReportType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID concertId,
            @RequestParam(required = false) UUID tourId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(businessReportService.previewWithAudit(type, from, to, concertId, tourId, authentication));
    }

    /**
     * Eksport pliku PDF, XLSX lub DOCX + audyt {@code report_runs} i {@code export_jobs}.
     */
    @GetMapping("/export")
    ResponseEntity<byte[]> export(
            @RequestParam BusinessReportType type,
            @RequestParam String format,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) UUID concertId,
            @RequestParam(required = false) UUID tourId,
            Authentication authentication
    ) {
        BusinessReportService.ReportExportResult result = businessReportService.exportWithAudit(
                type, format, from, to, concertId, tourId, authentication
        );
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.filename() + "\"")
                .contentType(MediaType.parseMediaType(result.contentType()))
                .body(result.body());
    }
}
