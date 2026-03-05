package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanDevice;
import com.bandhub.zsi.fan.dto.CreateFanDeviceRequest;
import com.bandhub.zsi.fan.dto.FanDeviceResponse;
import com.bandhub.zsi.fan.dto.UpdateFanDeviceRequest;
import com.bandhub.zsi.shared.api.PageResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class FanDeviceAdminService {

    private final FanDeviceRepository fanDeviceRepository;

    public FanDeviceAdminService(FanDeviceRepository fanDeviceRepository) {
        this.fanDeviceRepository = fanDeviceRepository;
    }

    public UUID create(CreateFanDeviceRequest request) {
        FanDevice device = FanDevice.create(request.fanId(), request.deviceToken(), request.platform(), request.appVersion(), request.lastSeenAt());
        return fanDeviceRepository.save(device).getId();
    }

    public void update(UUID id, UpdateFanDeviceRequest request) {
        FanDevice device = fanDeviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fan device not found: " + id));
        device.update(request.fanId(), request.deviceToken(), request.platform(), request.appVersion(), request.lastSeenAt());
    }

    public void delete(UUID id) {
        if (fanDeviceRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Fan device not found: " + id);
        }
        fanDeviceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public FanDeviceResponse getOne(UUID id) {
        return fanDeviceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Fan device not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<FanDeviceResponse> getAll() {
        return fanDeviceRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<FanDeviceResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = fanDeviceRepository.findPage(page, size, sortBy, sortDir, query);
        List<FanDeviceResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private FanDeviceResponse toResponse(FanDevice device) {
        return new FanDeviceResponse(
                device.getId(),
                device.getFanId(),
                device.getDeviceToken(),
                device.getPlatform(),
                device.getAppVersion(),
                device.getLastSeenAt(),
                device.getCreatedAt()
        );
    }
}
