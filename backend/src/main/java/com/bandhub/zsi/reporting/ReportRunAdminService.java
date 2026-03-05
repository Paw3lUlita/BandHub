package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ReportRun;
import com.bandhub.zsi.reporting.dto.CreateReportRunRequest;
import com.bandhub.zsi.reporting.dto.ReportRunResponse;
import com.bandhub.zsi.reporting.dto.UpdateReportRunRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var result = reportRunRepository.findPage(page, size, sortBy, sortDir, query);
        List<ReportRunResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private ReportRunResponse toResponse(ReportRun run) {
        return new ReportRunResponse(run.getId(), run.getReportName(), run.getRequestedBy(), run.getParametersJson(), run.getStatus(), run.getFileFormat(), run.getCreatedAt(), run.getCompletedAt());
    }
}
