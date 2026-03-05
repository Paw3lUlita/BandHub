package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.reporting.ReportRunRepository;
import com.bandhub.zsi.reporting.domain.ReportRun;
import com.bandhub.zsi.shared.api.PagedResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    @Override
    public PagedResult<ReportRun> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "reportName" -> "reportName";
            case "createdAt" -> "createdAt";
            default -> "reportName";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaReportRunRepository extends JpaRepository<ReportRun, UUID> {

    @Query("SELECT r FROM ReportRun r WHERE LOWER(COALESCE(r.reportName, '')) LIKE LOWER(:pattern) OR LOWER(COALESCE(r.status, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<ReportRun> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
