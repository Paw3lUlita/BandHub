package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.FanFavoriteRepository;
import com.bandhub.zsi.fan.domain.FanFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaFanFavoriteRepository extends JpaRepository<FanFavorite, UUID> {}
