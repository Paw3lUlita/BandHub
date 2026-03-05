package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.SetlistItemRepository;
import com.bandhub.zsi.fan.domain.SetlistItem;
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
class SqlSetlistItemRepository implements SetlistItemRepository {

    private final JpaSetlistItemRepository jpaRepository;

    SqlSetlistItemRepository(JpaSetlistItemRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SetlistItem save(SetlistItem setlistItem) { return jpaRepository.save(setlistItem); }

    @Override
    public Optional<SetlistItem> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<SetlistItem> findAll() { return jpaRepository.findAll(); }

    @Override
    public PagedResult<SetlistItem> findPage(int page, int size, String sortBy, String sortDir, String q) {
        String pattern = (q == null || q.isBlank()) ? "%" : "%" + q.trim().toLowerCase() + "%";
        Sort.Direction dir = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String prop = switch (sortBy) {
            case "songOrder" -> "songOrder";
            default -> "songTitle";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(dir, prop));
        var result = jpaRepository.findAllFiltered(pattern, pageable);
        return new PagedResult<>(result.getContent(), result.getTotalElements());
    }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaSetlistItemRepository extends JpaRepository<SetlistItem, UUID> {

    @Query("SELECT s FROM SetlistItem s WHERE LOWER(COALESCE(s.songTitle, '')) LIKE LOWER(:pattern)")
    org.springframework.data.domain.Page<SetlistItem> findAllFiltered(@Param("pattern") String pattern, Pageable pageable);
}
