package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ExportJob;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExportJobRepository {
    ExportJob save(ExportJob exportJob);
    Optional<ExportJob> findById(UUID id);
    List<ExportJob> findAll();
    PagedResult<ExportJob> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
