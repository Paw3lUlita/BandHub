package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanNotificationRead;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FanNotificationReadRepository {
    FanNotificationRead save(FanNotificationRead fanNotificationRead);
    Optional<FanNotificationRead> findById(UUID id);
    List<FanNotificationRead> findAll();
    void deleteById(UUID id);
}
