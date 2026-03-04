package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourCostCategory;
import com.bandhub.zsi.logistics.dto.CreateTourCategoryRequest;
import com.bandhub.zsi.logistics.dto.TourCategoryResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourCategoryRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourCostCategoryAdminService {

    private final TourCostCategoryRepository repository;

    public TourCostCategoryAdminService(TourCostCategoryRepository repository) { this.repository = repository; }

    public UUID create(CreateTourCategoryRequest request) {
        return repository.save(TourCostCategory.create(request.code(), request.name(), request.active())).getId();
    }

    public void update(UUID id, UpdateTourCategoryRequest request) {
        TourCostCategory category = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tour cost category not found: " + id));
        category.update(request.code(), request.name(), request.active());
    }

    public void delete(UUID id) { if (repository.findById(id).isEmpty()) throw new EntityNotFoundException("Tour cost category not found: " + id); repository.deleteById(id); }
    @Transactional(readOnly = true) public TourCategoryResponse getOne(UUID id) { return repository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Tour cost category not found: " + id)); }
    @Transactional(readOnly = true) public List<TourCategoryResponse> getAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public PageResponse<TourCategoryResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalized = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<TourCategoryResponse> filtered = getAll().stream()
                .filter(item -> normalized.isBlank()
                        || item.code().toLowerCase().contains(normalized)
                        || item.name().toLowerCase().contains(normalized))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<TourCategoryResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<TourCategoryResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<TourCategoryResponse> comparator = switch (sortBy) {
            case "code" -> Comparator.comparing(TourCategoryResponse::code, String.CASE_INSENSITIVE_ORDER);
            case "active" -> Comparator.comparing(TourCategoryResponse::active);
            default -> Comparator.comparing(TourCategoryResponse::name, String.CASE_INSENSITIVE_ORDER);
        };
        return descending ? comparator.reversed() : comparator;
    }
    private TourCategoryResponse toResponse(TourCostCategory c) { return new TourCategoryResponse(c.getId(), c.getCode(), c.getName(), c.isActive()); }
}
