package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.FanNotificationReadRepository;
import com.bandhub.zsi.fan.domain.FanNotificationRead;
import org.springframework.data.jpa.repository.JpaRepository;
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
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaFanNotificationReadRepository extends JpaRepository<FanNotificationRead, UUID> {}
