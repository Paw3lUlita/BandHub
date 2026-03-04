package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ReportRun;
import com.bandhub.zsi.reporting.dto.CreateReportRunRequest;
import com.bandhub.zsi.reporting.dto.ReportRunResponse;
import com.bandhub.zsi.reporting.dto.UpdateReportRunRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportRunAdminService {

    private final ReportRunRepository reportRunRepository;

    public ReportRunAdminService(ReportRunRepository reportRunRepository) {
        this.reportRunRepository = reportRunRepository;
    }

    public UUID create(CreateReportRunRequest request) {
        ReportRun run = ReportRun.create(request.reportName(), request.requestedBy(), request.parametersJson(), request.status(), request.fileFormat(), request.completedAt());
        return reportRunRepository.save(run).getId();
    }

    public void update(UUID id, UpdateReportRunRequest request) {
        ReportRun run = reportRunRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Report run not found: " + id));
        run.update(request.reportName(), request.requestedBy(), request.parametersJson(), request.status(), request.fileFormat(), request.completedAt());
    }

    public void delete(UUID id) {
        if (reportRunRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Report run not found: " + id);
        }
        reportRunRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ReportRunResponse getOne(UUID id) {
        return reportRunRepository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Report run not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ReportRunResponse> getAll() {
        return reportRunRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ReportRunResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<ReportRunResponse> filtered = reportRunRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.reportName().toLowerCase().contains(normalizedQuery)
                        || item.status().toLowerCase().contains(normalizedQuery)
                        || (item.requestedBy() != null && item.requestedBy().toLowerCase().contains(normalizedQuery)))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<ReportRunResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<ReportRunResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<ReportRunResponse> comparator = switch (sortBy) {
            case "reportName" -> Comparator.comparing(ReportRunResponse::reportName, String.CASE_INSENSITIVE_ORDER);
            case "status" -> Comparator.comparing(ReportRunResponse::status, String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(ReportRunResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(ReportRunResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private ReportRunResponse toResponse(ReportRun run) {
        return new ReportRunResponse(run.getId(), run.getReportName(), run.getRequestedBy(), run.getParametersJson(), run.getStatus(), run.getFileFormat(), run.getCreatedAt(), run.getCompletedAt());
    }
}
