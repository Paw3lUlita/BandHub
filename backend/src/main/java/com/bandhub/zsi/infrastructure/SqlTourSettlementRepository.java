package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.logistics.TourSettlementRepository;
import com.bandhub.zsi.logistics.domain.TourSettlement;
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
class SqlTourSettlementRepository implements TourSettlementRepository {

    private final JpaTourSettlementRepository jpaRepository;

    SqlTourSettlementRepository(JpaTourSettlementRepository jpaRepository) { this.jpaRepository = jpaRepository; }
    @Override public TourSettlement save(TourSettlement settlement) { return jpaRepository.save(settlement); }
    @Override public Optional<TourSettlement> findById(UUID id) { return jpaRepository.findById(id); }
    @Override public List<TourSettlement> findAll() { return jpaRepository.findAll(); }
    @Override
    public PagedResult<TourSettlement> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "balance" -> "balance";
            default -> "settledAt";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }
    @Override public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTourSettlementRepository extends JpaRepository<TourSettlement, UUID> {

    @Query("SELECT t FROM TourSettlement t WHERE LOWER(COALESCE(t.settledBy, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<TourSettlement> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
