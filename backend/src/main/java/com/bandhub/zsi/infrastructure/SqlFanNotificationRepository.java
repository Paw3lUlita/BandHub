package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.FanNotificationRepository;
import com.bandhub.zsi.fan.domain.FanNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlFanNotificationRepository implements FanNotificationRepository {

    private final JpaFanNotificationRepository jpaRepository;

    SqlFanNotificationRepository(JpaFanNotificationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FanNotification save(FanNotification fanNotification) { return jpaRepository.save(fanNotification); }

    @Override
    public Optional<FanNotification> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<FanNotification> findAll() { return jpaRepository.findAll(); }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaFanNotificationRepository extends JpaRepository<FanNotification, UUID> {}
