package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanFavorite;
import com.bandhub.zsi.fan.dto.CreateFanFavoriteRequest;
import com.bandhub.zsi.fan.dto.FanFavoriteResponse;
import com.bandhub.zsi.fan.dto.UpdateFanFavoriteRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<FanFavoriteResponse> filtered = fanFavoriteRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.fanId().toLowerCase().contains(normalizedQuery)
                        || item.favoriteType().toLowerCase().contains(normalizedQuery)
                        || item.referenceId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<FanFavoriteResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<FanFavoriteResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<FanFavoriteResponse> comparator = switch (sortBy) {
            case "fanId" -> Comparator.comparing(FanFavoriteResponse::fanId, String.CASE_INSENSITIVE_ORDER);
            case "favoriteType" -> Comparator.comparing(FanFavoriteResponse::favoriteType, String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(FanFavoriteResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(FanFavoriteResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
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
