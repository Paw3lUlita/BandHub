package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.Setlist;
import com.bandhub.zsi.fan.domain.SetlistItem;
import com.bandhub.zsi.fan.dto.CreateSetlistItemRequest;
import com.bandhub.zsi.fan.dto.SetlistItemResponse;
import com.bandhub.zsi.fan.dto.UpdateSetlistItemRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SetlistItemAdminService {

    private final SetlistItemRepository setlistItemRepository;
    private final SetlistRepository setlistRepository;

    public SetlistItemAdminService(SetlistItemRepository setlistItemRepository, SetlistRepository setlistRepository) {
        this.setlistItemRepository = setlistItemRepository;
        this.setlistRepository = setlistRepository;
    }

    public UUID create(CreateSetlistItemRequest request) {
        Setlist setlist = setlistRepository.findById(request.setlistId())
                .orElseThrow(() -> new EntityNotFoundException("Setlist not found: " + request.setlistId()));
        SetlistItem item = SetlistItem.create(setlist, request.songTitle(), request.songOrder(), request.durationSeconds());
        return setlistItemRepository.save(item).getId();
    }

    public void update(UUID id, UpdateSetlistItemRequest request) {
        SetlistItem item = setlistItemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Setlist item not found: " + id));
        Setlist setlist = setlistRepository.findById(request.setlistId())
                .orElseThrow(() -> new EntityNotFoundException("Setlist not found: " + request.setlistId()));
        item.update(setlist, request.songTitle(), request.songOrder(), request.durationSeconds());
    }

    public void delete(UUID id) {
        if (setlistItemRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Setlist item not found: " + id);
        }
        setlistItemRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SetlistItemResponse getOne(UUID id) {
        return setlistItemRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Setlist item not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<SetlistItemResponse> getAll() {
        return setlistItemRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<SetlistItemResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<SetlistItemResponse> filtered = setlistItemRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.songTitle().toLowerCase().contains(normalizedQuery)
                        || item.setlistId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<SetlistItemResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<SetlistItemResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<SetlistItemResponse> comparator = switch (sortBy) {
            case "songOrder" -> Comparator.comparing(SetlistItemResponse::songOrder);
            case "songTitle" -> Comparator.comparing(SetlistItemResponse::songTitle, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(SetlistItemResponse::songOrder);
        };
        return descending ? comparator.reversed() : comparator;
    }

    private SetlistItemResponse toResponse(SetlistItem item) {
        return new SetlistItemResponse(
                item.getId(),
                item.getSetlist().getId(),
                item.getSongTitle(),
                item.getSongOrder(),
                item.getDurationSeconds()
        );
    }
}
