package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanNotification;
import com.bandhub.zsi.fan.dto.CreateFanNotificationRequest;
import com.bandhub.zsi.fan.dto.FanNotificationResponse;
import com.bandhub.zsi.fan.dto.UpdateFanNotificationRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FanNotificationAdminService {

    private final FanNotificationRepository fanNotificationRepository;

    public FanNotificationAdminService(FanNotificationRepository fanNotificationRepository) {
        this.fanNotificationRepository = fanNotificationRepository;
    }

    public UUID create(CreateFanNotificationRequest request) {
        FanNotification notification = FanNotification.create(
                request.id(),
                request.fanId(),
                request.broadcast(),
                request.title(),
                request.message(),
                request.module()
        );
        return fanNotificationRepository.save(notification).getId();
    }

    public void update(UUID id, UpdateFanNotificationRequest request) {
        FanNotification notification = fanNotificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fan notification not found: " + id));
        notification.update(request.fanId(), request.broadcast(), request.title(), request.message(), request.module());
    }

    public void delete(UUID id) {
        if (fanNotificationRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Fan notification not found: " + id);
        }
        fanNotificationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public FanNotificationResponse getOne(UUID id) {
        return fanNotificationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Fan notification not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<FanNotificationResponse> getAll() {
        return fanNotificationRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FanNotificationResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<FanNotificationResponse> filtered = fanNotificationRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.title().toLowerCase().contains(normalizedQuery)
                        || item.message().toLowerCase().contains(normalizedQuery)
                        || (item.fanId() != null && item.fanId().toLowerCase().contains(normalizedQuery)))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<FanNotificationResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<FanNotificationResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<FanNotificationResponse> comparator = switch (sortBy) {
            case "title" -> Comparator.comparing(FanNotificationResponse::title, String.CASE_INSENSITIVE_ORDER);
            case "createdAt" -> Comparator.comparing(FanNotificationResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(FanNotificationResponse::createdAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private FanNotificationResponse toResponse(FanNotification notification) {
        return new FanNotificationResponse(
                notification.getId(),
                notification.getFanId(),
                notification.isBroadcast(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getModule(),
                notification.getCreatedAt()
        );
    }
}
