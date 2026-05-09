package com.bandhub.zsi.reporting;

import com.bandhub.zsi.ecommerce.MerchReportService;
import com.bandhub.zsi.ecommerce.dto.MerchSalesSnapshotResponse;
import com.bandhub.zsi.logistics.LogisticsAdminService;
import com.bandhub.zsi.logistics.TourSettlementAdminService;
import com.bandhub.zsi.logistics.dto.TourProfitabilityResponse;
import com.bandhub.zsi.logistics.dto.TourSettlementResponse;
import com.bandhub.zsi.reporting.domain.DocxTemplate;
import com.bandhub.zsi.reporting.domain.ExportJob;
import com.bandhub.zsi.reporting.domain.ReportRun;
import com.bandhub.zsi.reporting.dto.BusinessReportPreviewResponse;
import com.bandhub.zsi.reporting.dto.BusinessReportType;
import com.bandhub.zsi.reporting.dto.DocxTemplateModuleCodes;
import com.bandhub.zsi.reporting.dto.TourSettlementDocxPreviewPayload;
import com.bandhub.zsi.reporting.infrastructure.ReportBinaryRenderer;
import com.bandhub.zsi.reporting.infrastructure.TourSettlementDocxPlaceholderBuilder;
import com.bandhub.zsi.reporting.infrastructure.TourSettlementDocxRenderer;
import com.bandhub.zsi.shared.security.AuthenticationDisplayName;
import com.bandhub.zsi.ticketing.TicketingReportingService;
import com.bandhub.zsi.ticketing.dto.TicketingEventSnapshotResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Orkiestracja raportów biznesowych: podgląd JSON, eksport PDF/XLSX/DOCX, zapis audytu w {@code report_runs} i {@code export_jobs}.
 */
@Service
@Transactional
@Slf4j
class BusinessReportService {

    private static final String DOCX_SUBDIR = "docx-templates";

    private final MerchReportService merchReportService;
    private final TicketingReportingService ticketingReportingService;
    private final LogisticsAdminService logisticsAdminService;
    private final TourSettlementAdminService tourSettlementAdminService;
    private final DocxTemplateRepository docxTemplateRepository;
    private final ReportRunRepository reportRunRepository;
    private final ExportJobRepository exportJobRepository;
    private final ReportBinaryRenderer binaryRenderer;
    private final TourSettlementDocxRenderer docxRenderer;
    private final Path docxStorageLocation;
    private final ObjectMapper objectMapper;

    BusinessReportService(
            MerchReportService merchReportService,
            TicketingReportingService ticketingReportingService,
            LogisticsAdminService logisticsAdminService,
            TourSettlementAdminService tourSettlementAdminService,
            DocxTemplateRepository docxTemplateRepository,
            ReportRunRepository reportRunRepository,
            ExportJobRepository exportJobRepository,
            ObjectMapper objectMapper,
            @Value("${app.upload.dir}") String uploadDir
    ) {
        this.merchReportService = merchReportService;
        this.ticketingReportingService = ticketingReportingService;
        this.logisticsAdminService = logisticsAdminService;
        this.tourSettlementAdminService = tourSettlementAdminService;
        this.docxTemplateRepository = docxTemplateRepository;
        this.reportRunRepository = reportRunRepository;
        this.exportJobRepository = exportJobRepository;
        this.objectMapper = objectMapper;
        this.binaryRenderer = new ReportBinaryRenderer();
        this.docxRenderer = new TourSettlementDocxRenderer();
        this.docxStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize().resolve(DOCX_SUBDIR);
    }

    public BusinessReportPreviewResponse preview(
            BusinessReportType type,
            LocalDate from,
            LocalDate to,
            UUID concertId,
            UUID tourId
    ) {
        return switch (type) {
            case MERCH -> {
                LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
                LocalDateTime toDt = to != null ? to.atTime(23, 59, 59) : null;
                MerchSalesSnapshotResponse data = merchReportService.getMerchSalesSnapshot(fromDt, toDt);
                yield new BusinessReportPreviewResponse(type, data);
            }
            case TICKETING_EVENT -> {
                if (concertId == null) {
                    throw new IllegalArgumentException("concertId is required for TICKETING_EVENT report");
                }
                TicketingEventSnapshotResponse data = ticketingReportingService.eventSnapshot(concertId);
                yield new BusinessReportPreviewResponse(type, data);
            }
            case TOUR_PROFITABILITY -> {
                if (tourId == null) {
                    throw new IllegalArgumentException("tourId is required for TOUR_PROFITABILITY report");
                }
                TourProfitabilityResponse data = logisticsAdminService.getProfitability(tourId);
                yield new BusinessReportPreviewResponse(type, data);
            }
            case TOUR_SETTLEMENT_DOCX -> {
                if (tourId == null) {
                    throw new IllegalArgumentException("tourId is required for TOUR_SETTLEMENT_DOCX report");
                }
                Optional<TourSettlementResponse> settlement = tourSettlementAdminService.findByTourId(tourId);
                TourProfitabilityResponse profit = logisticsAdminService.getProfitability(tourId);
                Optional<DocxTemplate> active = docxTemplateRepository.findActiveByModuleCode(DocxTemplateModuleCodes.TOUR_SETTLEMENT);
                yield new BusinessReportPreviewResponse(type, new TourSettlementDocxPreviewPayload(
                        settlement.isPresent(),
                        settlement.orElse(null),
                        profit,
                        active.map(DocxTemplate::getId).orElse(null),
                        active.map(DocxTemplate::getName).orElse(null)
                ));
            }
        };
    }

    /**
     * Podgląd z zapisem rekordu {@link ReportRun} (status COMPLETED, format PREVIEW).
     */
    public BusinessReportPreviewResponse previewWithAudit(
            BusinessReportType type,
            LocalDate from,
            LocalDate to,
            UUID concertId,
            UUID tourId,
            Authentication authentication
    ) {
        String user = AuthenticationDisplayName.resolve(authentication);
        String paramsJson = buildParamsJson(type, from, to, concertId, tourId, null);
        BusinessReportPreviewResponse body = preview(type, from, to, concertId, tourId);
        ReportRun run = ReportRun.create(
                reportDisplayName(type),
                user,
                paramsJson,
                "COMPLETED",
                "PREVIEW",
                LocalDateTime.now()
        );
        reportRunRepository.save(run);
        log.info("Report preview recorded: type={} requestedBy={}", type, user);
        return body;
    }

    public ReportExportResult exportWithAudit(
            BusinessReportType type,
            String formatRaw,
            LocalDate from,
            LocalDate to,
            UUID concertId,
            UUID tourId,
            Authentication authentication
    ) {
        String format = normalizeFormat(formatRaw);
        String user = AuthenticationDisplayName.resolve(authentication);
        DocxTemplate activeDocx = null;
        if (type == BusinessReportType.TOUR_SETTLEMENT_DOCX) {
            activeDocx = docxTemplateRepository.findActiveByModuleCode(DocxTemplateModuleCodes.TOUR_SETTLEMENT)
                    .orElseThrow(() -> new IllegalStateException(
                            "No active DOCX template for TOUR_SETTLEMENT. Upload a .docx in admin and activate it."));
        }
        String paramsJson = buildParamsJson(type, from, to, concertId, tourId,
                activeDocx != null ? activeDocx.getId() : null);
        ReportRun run = ReportRun.create(
                reportDisplayName(type),
                user,
                paramsJson,
                "RUNNING",
                format.toUpperCase(Locale.ROOT),
                null
        );
        UUID runId = reportRunRepository.save(run).getId();
        String filename = buildFilename(type, format, concertId, tourId);
        try {
            byte[] bytes = exportBytes(type, format, from, to, concertId, tourId, activeDocx);
            ReportRun persisted = reportRunRepository.findById(runId)
                    .orElseThrow(() -> new EntityNotFoundException("Report run not found: " + runId));
            persisted.update(
                    persisted.getReportName(),
                    persisted.getRequestedBy(),
                    persisted.getParametersJson(),
                    "COMPLETED",
                    persisted.getFileFormat(),
                    LocalDateTime.now()
            );
            reportRunRepository.save(persisted);

            ExportJob job = ExportJob.create(
                    "reporting",
                    type.name() + ":" + format,
                    "COMPLETED",
                    user,
                    "inline://" + filename,
                    LocalDateTime.now()
            );
            exportJobRepository.save(job);
            log.info("Report export completed: type={} format={} requestedBy={}", type, format, user);
            return new ReportExportResult(bytes, contentTypeFor(format), filename);
        } catch (RuntimeException ex) {
            ReportRun persisted = reportRunRepository.findById(runId).orElse(null);
            if (persisted != null) {
                persisted.update(
                        persisted.getReportName(),
                        persisted.getRequestedBy(),
                        persisted.getParametersJson(),
                        "FAILED",
                        persisted.getFileFormat(),
                        LocalDateTime.now()
                );
                reportRunRepository.save(persisted);
            }
            log.error("Report export failed: type={} format={}", type, format, ex);
            throw ex;
        }
    }

    private byte[] exportBytes(
            BusinessReportType type,
            String format,
            LocalDate from,
            LocalDate to,
            UUID concertId,
            UUID tourId,
            DocxTemplate activeDocx
    ) {
        boolean pdf = "pdf".equals(format);
        return switch (type) {
            case MERCH -> {
                if ("docx".equals(format)) {
                    throw new IllegalArgumentException("docx is not supported for MERCH");
                }
                LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
                LocalDateTime toDt = to != null ? to.atTime(23, 59, 59) : null;
                MerchSalesSnapshotResponse data = merchReportService.getMerchSalesSnapshot(fromDt, toDt);
                yield pdf ? binaryRenderer.merchPdf(data) : binaryRenderer.merchXlsx(data);
            }
            case TICKETING_EVENT -> {
                if ("docx".equals(format)) {
                    throw new IllegalArgumentException("docx is not supported for TICKETING_EVENT");
                }
                if (concertId == null) {
                    throw new IllegalArgumentException("concertId is required for TICKETING_EVENT report");
                }
                TicketingEventSnapshotResponse data = ticketingReportingService.eventSnapshot(concertId);
                yield pdf ? binaryRenderer.ticketingEventPdf(data) : binaryRenderer.ticketingEventXlsx(data);
            }
            case TOUR_PROFITABILITY -> {
                if ("docx".equals(format)) {
                    throw new IllegalArgumentException("docx is not supported for TOUR_PROFITABILITY; use TOUR_SETTLEMENT_DOCX");
                }
                if (tourId == null) {
                    throw new IllegalArgumentException("tourId is required for TOUR_PROFITABILITY report");
                }
                TourProfitabilityResponse data = logisticsAdminService.getProfitability(tourId);
                yield pdf ? binaryRenderer.tourProfitabilityPdf(data) : binaryRenderer.tourProfitabilityXlsx(data);
            }
            case TOUR_SETTLEMENT_DOCX -> {
                if (!"docx".equals(format)) {
                    throw new IllegalArgumentException("TOUR_SETTLEMENT_DOCX requires format=docx");
                }
                if (tourId == null) {
                    throw new IllegalArgumentException("tourId is required for TOUR_SETTLEMENT_DOCX");
                }
                if (activeDocx == null) {
                    throw new IllegalStateException("Active DOCX template missing");
                }
                Path file = docxStorageLocation.resolve(activeDocx.getFilePath()).normalize();
                if (!file.startsWith(docxStorageLocation)) {
                    throw new IllegalStateException("Invalid template path");
                }
                TourProfitabilityResponse profit = logisticsAdminService.getProfitability(tourId);
                Optional<TourSettlementResponse> settlement = tourSettlementAdminService.findByTourId(tourId);
                Map<String, String> placeholders = TourSettlementDocxPlaceholderBuilder.build(settlement, profit);
                try (InputStream in = Files.newInputStream(file)) {
                    yield docxRenderer.render(in, placeholders);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to read or render DOCX template", e);
                }
            }
        };
    }

    private static String normalizeFormat(String formatRaw) {
        if (formatRaw == null || formatRaw.isBlank()) {
            throw new IllegalArgumentException("format is required (pdf, xlsx, or docx)");
        }
        String f = formatRaw.trim().toLowerCase(Locale.ROOT);
        if (!f.equals("pdf") && !f.equals("xlsx") && !f.equals("docx")) {
            throw new IllegalArgumentException("Unsupported format: " + formatRaw + " (use pdf, xlsx, or docx)");
        }
        return f;
    }

    private static String contentTypeFor(String format) {
        if ("pdf".equals(format)) {
            return "application/pdf";
        }
        if ("docx".equals(format)) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    }

    private static String buildFilename(BusinessReportType type, String format, UUID concertId, UUID tourId) {
        String base = switch (type) {
            case MERCH -> "merch-sales";
            case TICKETING_EVENT -> "ticketing-event-" + (concertId != null ? concertId : "unknown");
            case TOUR_PROFITABILITY -> "tour-profitability-" + (tourId != null ? tourId : "unknown");
            case TOUR_SETTLEMENT_DOCX -> "tour-settlement-" + (tourId != null ? tourId : "unknown");
        };
        String ext = "pdf".equals(format) ? ".pdf" : "docx".equals(format) ? ".docx" : ".xlsx";
        return base + ext;
    }

    private static String reportDisplayName(BusinessReportType type) {
        return switch (type) {
            case MERCH -> "Merch — podsumowanie sprzedaży";
            case TICKETING_EVENT -> "Ticketing — podsumowanie wydarzenia";
            case TOUR_PROFITABILITY -> "Logistyka — rentowność trasy";
            case TOUR_SETTLEMENT_DOCX -> "Logistyka — rozliczenie trasy (DOCX)";
        };
    }

    private String buildParamsJson(
            BusinessReportType type,
            LocalDate from,
            LocalDate to,
            UUID concertId,
            UUID tourId,
            UUID docxTemplateId
    ) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("reportType", type.name());
        if (from != null) {
            m.put("from", from.toString());
        }
        if (to != null) {
            m.put("to", to.toString());
        }
        if (concertId != null) {
            m.put("concertId", concertId.toString());
        }
        if (tourId != null) {
            m.put("tourId", tourId.toString());
        }
        if (docxTemplateId != null) {
            m.put("docxTemplateId", docxTemplateId.toString());
        }
        try {
            return objectMapper.writeValueAsString(m);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize report parameters", e);
        }
    }

    public record ReportExportResult(byte[] body, String contentType, String filename) {
    }
}
