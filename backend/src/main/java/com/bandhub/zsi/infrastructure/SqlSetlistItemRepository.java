package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.SetlistItemRepository;
import com.bandhub.zsi.fan.domain.SetlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaSetlistItemRepository extends JpaRepository<SetlistItem, UUID> {}
