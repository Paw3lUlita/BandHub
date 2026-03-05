package com.bandhub.zsi.logistics;

import com.bandhub.zsi.logistics.domain.TourRevenueCategory;
import com.bandhub.zsi.logistics.dto.CreateTourCategoryRequest;
import com.bandhub.zsi.logistics.dto.TourCategoryResponse;
import com.bandhub.zsi.logistics.dto.UpdateTourCategoryRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TourRevenueCategoryAdminService {

    private final TourRevenueCategoryRepository repository;

    public TourRevenueCategoryAdminService(TourRevenueCategoryRepository repository) { this.repository = repository; }

    public UUID create(CreateTourCategoryRequest request) {
        return repository.save(TourRevenueCategory.create(request.code(), request.name(), request.active())).getId();
    }

    public void update(UUID id, UpdateTourCategoryRequest request) {
        TourRevenueCategory category = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Tour revenue category not found: " + id));
        category.update(request.code(), request.name(), request.active());
    }

    public void delete(UUID id) { if (repository.findById(id).isEmpty()) throw new EntityNotFoundException("Tour revenue category not found: " + id); repository.deleteById(id); }
    @Transactional(readOnly = true) public TourCategoryResponse getOne(UUID id) { return repository.findById(id).map(this::toResponse).orElseThrow(() -> new EntityNotFoundException("Tour revenue category not found: " + id)); }
    @Transactional(readOnly = true) public List<TourCategoryResponse> getAll() { return repository.findAll().stream().map(this::toResponse).toList(); }
    @Transactional(readOnly = true)
    public PageResponse<TourCategoryResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = repository.findPage(page, size, sortBy, sortDir, query);
        List<TourCategoryResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }
    private TourCategoryResponse toResponse(TourRevenueCategory c) { return new TourCategoryResponse(c.getId(), c.getCode(), c.getName(), c.isActive()); }
}
