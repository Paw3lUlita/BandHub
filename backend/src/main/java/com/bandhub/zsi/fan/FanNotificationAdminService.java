package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanNotification;
import com.bandhub.zsi.fan.dto.CreateFanNotificationRequest;
import com.bandhub.zsi.fan.dto.FanNotificationResponse;
import com.bandhub.zsi.fan.dto.UpdateFanNotificationRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        var result = fanNotificationRepository.findPage(page, size, sortBy, sortDir, query);
        List<FanNotificationResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
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
