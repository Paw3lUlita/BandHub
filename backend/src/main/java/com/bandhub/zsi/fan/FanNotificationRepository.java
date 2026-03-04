package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanNotification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FanNotificationRepository {
    FanNotification save(FanNotification fanNotification);
    Optional<FanNotification> findById(UUID id);
    List<FanNotification> findAll();
    void deleteById(UUID id);
}
