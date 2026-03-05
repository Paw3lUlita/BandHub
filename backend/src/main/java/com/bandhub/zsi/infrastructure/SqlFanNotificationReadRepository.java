package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.FanNotificationReadRepository;
import com.bandhub.zsi.fan.domain.FanNotificationRead;
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
class SqlFanNotificationReadRepository implements FanNotificationReadRepository {

    private final JpaFanNotificationReadRepository jpaRepository;

    SqlFanNotificationReadRepository(JpaFanNotificationReadRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FanNotificationRead save(FanNotificationRead fanNotificationRead) { return jpaRepository.save(fanNotificationRead); }

    @Override
    public Optional<FanNotificationRead> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<FanNotificationRead> findAll() { return jpaRepository.findAll(); }

    @Override
    public PagedResult<FanNotificationRead> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "readAt" -> "readAt";
            default -> "fanId";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaFanNotificationReadRepository extends JpaRepository<FanNotificationRead, UUID> {

    @Query("SELECT f FROM FanNotificationRead f WHERE LOWER(COALESCE(f.fanId, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<FanNotificationRead> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
