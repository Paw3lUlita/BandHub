package com.bandhub.zsi.reporting;

import com.bandhub.zsi.reporting.domain.ExportJob;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExportJobRepository {
    ExportJob save(ExportJob exportJob);
    Optional<ExportJob> findById(UUID id);
    List<ExportJob> findAll();
    void deleteById(UUID id);
}
