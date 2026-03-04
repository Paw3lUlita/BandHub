package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.SetlistItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetlistItemRepository {
    SetlistItem save(SetlistItem setlistItem);
    Optional<SetlistItem> findById(UUID id);
    List<SetlistItem> findAll();
    void deleteById(UUID id);
}
