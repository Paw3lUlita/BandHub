package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ExportJob;
import com.bandhub.zsi.reporting.dto.CreateExportJobRequest;
import com.bandhub.zsi.reporting.dto.ExportJobResponse;
import com.bandhub.zsi.reporting.dto.UpdateExportJobRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ExportJobAdminService {

    private final ExportJobRepository exportJobRepository;

    public ExportJobAdminService(ExportJobRepository exportJobRepository) {
        this.exportJobRepository = exportJobRepository;
    }

    public UUID create(CreateExportJobRequest request) {
        ExportJob job = ExportJob.create(request.module(), request.entityName(), request.status(), request.requestedBy(), request.filePath(), request.completedAt());
        return exportJobRepository.save(job).getId();
    }

    public void update(UUID id, UpdateExportJobRequest request) {
        ExportJob job = exportJobRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Export job not found: " + id));
        job.update(request.module(), request.entityName(), request.status(), request.requestedBy(), request.filePath(), request.completedAt());
    }

    public void delete(UUID id) {
        if (exportJobRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Export job not found: " + id);
        }
        exportJobRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ExportJobResponse getOne(UUID id) {
        return exportJobRepository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Export job not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<ExportJobResponse> getAll() {
        return exportJobRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<ExportJobResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = exportJobRepository.findPage(page, size, sortBy, sortDir, query);
        List<ExportJobResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private ExportJobResponse toResponse(ExportJob job) {
        return new ExportJobResponse(job.getId(), job.getModule(), job.getEntityName(), job.getStatus(), job.getRequestedBy(), job.getFilePath(), job.getCreatedAt(), job.getCompletedAt());
    }
}
