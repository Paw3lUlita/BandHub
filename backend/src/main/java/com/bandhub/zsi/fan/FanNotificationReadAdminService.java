package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanNotification;
import com.bandhub.zsi.fan.domain.FanNotificationRead;
import com.bandhub.zsi.fan.dto.CreateFanNotificationReadRequest;
import com.bandhub.zsi.fan.dto.FanNotificationReadResponse;
import com.bandhub.zsi.fan.dto.UpdateFanNotificationReadRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FanNotificationReadAdminService {

    private final FanNotificationReadRepository fanNotificationReadRepository;
    private final FanNotificationRepository fanNotificationRepository;

    public FanNotificationReadAdminService(FanNotificationReadRepository fanNotificationReadRepository, FanNotificationRepository fanNotificationRepository) {
        this.fanNotificationReadRepository = fanNotificationReadRepository;
        this.fanNotificationRepository = fanNotificationRepository;
    }

    public UUID create(CreateFanNotificationReadRequest request) {
        FanNotification notification = fanNotificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new EntityNotFoundException("Fan notification not found: " + request.notificationId()));
        FanNotificationRead read = FanNotificationRead.create(notification, request.fanId(), request.readAt());
        return fanNotificationReadRepository.save(read).getId();
    }

    public void update(UUID id, UpdateFanNotificationReadRequest request) {
        FanNotificationRead read = fanNotificationReadRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fan notification read not found: " + id));
        FanNotification notification = fanNotificationRepository.findById(request.notificationId())
                .orElseThrow(() -> new EntityNotFoundException("Fan notification not found: " + request.notificationId()));
        read.update(notification, request.fanId(), request.readAt());
    }

    public void delete(UUID id) {
        if (fanNotificationReadRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Fan notification read not found: " + id);
        }
        fanNotificationReadRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public FanNotificationReadResponse getOne(UUID id) {
        return fanNotificationReadRepository.findById(id).map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Fan notification read not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<FanNotificationReadResponse> getAll() {
        return fanNotificationReadRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FanNotificationReadResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);
        List<FanNotificationReadResponse> filtered = fanNotificationReadRepository.findAll().stream()
                .map(this::toResponse)
                .filter(item -> normalizedQuery.isBlank()
                        || item.fanId().toLowerCase().contains(normalizedQuery)
                        || item.notificationId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<FanNotificationReadResponse> content = fromIndex >= filtered.size() ? List.of() : filtered.subList(fromIndex, toIndex);
        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<FanNotificationReadResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<FanNotificationReadResponse> comparator = switch (sortBy) {
            case "fanId" -> Comparator.comparing(FanNotificationReadResponse::fanId, String.CASE_INSENSITIVE_ORDER);
            case "readAt" -> Comparator.comparing(FanNotificationReadResponse::readAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(FanNotificationReadResponse::readAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
    }

    private FanNotificationReadResponse toResponse(FanNotificationRead read) {
        return new FanNotificationReadResponse(
                read.getId(),
                read.getNotification().getId(),
                read.getFanId(),
                read.getReadAt()
        );
    }
}
