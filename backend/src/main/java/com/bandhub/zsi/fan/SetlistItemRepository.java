package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.SetlistItem;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetlistItemRepository {
    SetlistItem save(SetlistItem setlistItem);
    Optional<SetlistItem> findById(UUID id);
    List<SetlistItem> findAll();
    PagedResult<SetlistItem> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
