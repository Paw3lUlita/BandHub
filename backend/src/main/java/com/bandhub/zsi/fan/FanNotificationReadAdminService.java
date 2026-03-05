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
        var result = fanNotificationReadRepository.findPage(page, size, sortBy, sortDir, query);
        List<FanNotificationReadResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
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
