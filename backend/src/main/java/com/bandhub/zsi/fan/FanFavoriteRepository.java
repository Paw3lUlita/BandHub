package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanFavorite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FanFavoriteRepository {
    FanFavorite save(FanFavorite fanFavorite);
    Optional<FanFavorite> findById(UUID id);
    List<FanFavorite> findAll();
    void deleteById(UUID id);
}
