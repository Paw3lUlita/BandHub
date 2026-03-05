package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.Setlist;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetlistRepository {
    Setlist save(Setlist setlist);
    Optional<Setlist> findById(UUID id);
    List<Setlist> findAll();
    PagedResult<Setlist> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
