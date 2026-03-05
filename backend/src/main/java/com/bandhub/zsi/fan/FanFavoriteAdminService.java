package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanFavorite;
import com.bandhub.zsi.fan.dto.CreateFanFavoriteRequest;
import com.bandhub.zsi.fan.dto.FanFavoriteResponse;
import com.bandhub.zsi.fan.dto.UpdateFanFavoriteRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FanFavoriteAdminService {

    private final FanFavoriteRepository fanFavoriteRepository;

    public FanFavoriteAdminService(FanFavoriteRepository fanFavoriteRepository) {
        this.fanFavoriteRepository = fanFavoriteRepository;
    }

    public UUID create(CreateFanFavoriteRequest request) {
        FanFavorite favorite = FanFavorite.create(request.fanId(), request.favoriteType(), request.referenceId());
        return fanFavoriteRepository.save(favorite).getId();
    }

    public void update(UUID id, UpdateFanFavoriteRequest request) {
        FanFavorite favorite = fanFavoriteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fan favorite not found: " + id));
        favorite.update(request.fanId(), request.favoriteType(), request.referenceId());
    }

    public void delete(UUID id) {
        if (fanFavoriteRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Fan favorite not found: " + id);
        }
        fanFavoriteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public FanFavoriteResponse getOne(UUID id) {
        return fanFavoriteRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Fan favorite not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<FanFavoriteResponse> getAll() {
        return fanFavoriteRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FanFavoriteResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = fanFavoriteRepository.findPage(page, size, sortBy, sortDir, query);
        List<FanFavoriteResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private FanFavoriteResponse toResponse(FanFavorite favorite) {
        return new FanFavoriteResponse(
                favorite.getId(),
                favorite.getFanId(),
                favorite.getFavoriteType(),
                favorite.getReferenceId(),
                favorite.getCreatedAt()
        );
    }
}
