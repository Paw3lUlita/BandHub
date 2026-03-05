package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.FanFavoriteRepository;
import com.bandhub.zsi.fan.domain.FanFavorite;
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
class SqlFanFavoriteRepository implements FanFavoriteRepository {

    private final JpaFanFavoriteRepository jpaRepository;

    SqlFanFavoriteRepository(JpaFanFavoriteRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FanFavorite save(FanFavorite fanFavorite) { return jpaRepository.save(fanFavorite); }

    @Override
    public Optional<FanFavorite> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<FanFavorite> findAll() { return jpaRepository.findAll(); }

    @Override
    public PagedResult<FanFavorite> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "createdAt" -> "createdAt";
            default -> "fanId";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaFanFavoriteRepository extends JpaRepository<FanFavorite, UUID> {

    @Query("SELECT f FROM FanFavorite f WHERE LOWER(COALESCE(f.fanId, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<FanFavorite> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
