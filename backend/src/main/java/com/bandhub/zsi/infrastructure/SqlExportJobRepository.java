package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.reporting.ExportJobRepository;
import com.bandhub.zsi.reporting.domain.ExportJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlExportJobRepository implements ExportJobRepository {

    private final JpaExportJobRepository jpaRepository;

    SqlExportJobRepository(JpaExportJobRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public ExportJob save(ExportJob exportJob) { return jpaRepository.save(exportJob); }
    @Override public Optional<ExportJob> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<ExportJob> findAll() { return jpaRepository.findAll(); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaExportJobRepository extends JpaRepository<ExportJob, UUID> {}
