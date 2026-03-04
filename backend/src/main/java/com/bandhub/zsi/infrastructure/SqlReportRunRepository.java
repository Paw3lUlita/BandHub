package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.reporting.ReportRunRepository;
import com.bandhub.zsi.reporting.domain.ReportRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlReportRunRepository implements ReportRunRepository {

    private final JpaReportRunRepository jpaRepository;

    SqlReportRunRepository(JpaReportRunRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public ReportRun save(ReportRun reportRun) { return jpaRepository.save(reportRun); }
    @Override public Optional<ReportRun> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<ReportRun> findAll() { return jpaRepository.findAll(); }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaReportRunRepository extends JpaRepository<ReportRun, UUID> {}
