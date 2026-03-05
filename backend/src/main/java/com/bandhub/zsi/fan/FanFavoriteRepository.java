package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanFavorite;
import com.bandhub.zsi.shared.api.PagedResult;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FanFavoriteRepository {
    FanFavorite save(FanFavorite fanFavorite);
    Optional<FanFavorite> findById(UUID id);
    List<FanFavorite> findAll();
    PagedResult<FanFavorite> findPage(int page, int size, String sortBy, String sortDir, String q);
    void deleteById(UUID id);
}
