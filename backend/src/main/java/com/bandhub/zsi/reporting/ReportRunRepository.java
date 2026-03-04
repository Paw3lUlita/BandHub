package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ReportRun;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReportRunRepository {
    ReportRun save(ReportRun reportRun);
    Optional<ReportRun> findById(UUID id);
    List<ReportRun> findAll();
    void deleteById(UUID id);
}
