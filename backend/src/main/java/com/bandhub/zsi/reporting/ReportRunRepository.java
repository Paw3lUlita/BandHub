package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ReportRun;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRunRepository {
    ReportRun save(ReportRun reportRun);
    Optional<ReportRun> findById(UUID id);
    List<ReportRun> findAll();
    PagedResult<ReportRun> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
