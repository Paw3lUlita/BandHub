package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.SetlistRepository;
import com.bandhub.zsi.fan.domain.Setlist;
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
class SqlSetlistRepository implements SetlistRepository {

    private final JpaSetlistRepository jpaRepository;

    SqlSetlistRepository(JpaSetlistRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Setlist save(Setlist setlist) {
        return jpaRepository.save(setlist);
    }

    @Override
    public Optional<Setlist> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Setlist> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public PagedResult<Setlist> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "createdAt" -> "createdAt";
            default -> "title";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaSetlistRepository extends JpaRepository<Setlist, UUID> {

    @Query("SELECT s FROM Setlist s WHERE LOWER(COALESCE(s.title, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<Setlist> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
