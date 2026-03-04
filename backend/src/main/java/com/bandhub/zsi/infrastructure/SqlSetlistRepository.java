package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.SetlistRepository;
import com.bandhub.zsi.fan.domain.Setlist;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaSetlistRepository extends JpaRepository<Setlist, UUID> {}
