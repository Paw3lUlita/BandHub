package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanNotificationRead;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FanNotificationReadRepository {
    FanNotificationRead save(FanNotificationRead fanNotificationRead);
    Optional<FanNotificationRead> findById(UUID id);
    List<FanNotificationRead> findAll();
    PagedResult<FanNotificationRead> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
