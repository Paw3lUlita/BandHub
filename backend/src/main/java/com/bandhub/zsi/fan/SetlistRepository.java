package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.Setlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SetlistRepository {
    Setlist save(Setlist setlist);
    Optional<Setlist> findById(UUID id);
    List<Setlist> findAll();
    void deleteById(UUID id);
}
