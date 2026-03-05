package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.reporting.ExportJobRepository;
import com.bandhub.zsi.reporting.domain.ExportJob;
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
class SqlExportJobRepository implements ExportJobRepository {

    private final JpaExportJobRepository jpaRepository;

    SqlExportJobRepository(JpaExportJobRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public ExportJob save(ExportJob exportJob) { return jpaRepository.save(exportJob); }
    @Override public Optional<ExportJob> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<ExportJob> findAll() { return jpaRepository.findAll(); }
    @Override
    public PagedResult<ExportJob> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "module" -> "module";
            case "createdAt" -> "createdAt";
            default -> "module";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaExportJobRepository extends JpaRepository<ExportJob, UUID> {

    @Query("SELECT e FROM ExportJob e WHERE LOWER(COALESCE(e.module, '')) LIKE LOWER(:pattern) OR LOWER(COALESCE(e.status, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<ExportJob> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
