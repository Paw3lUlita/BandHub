package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.domain.FanDevice;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FanDeviceRepository {
    FanDevice save(FanDevice fanDevice);
    Optional<FanDevice> findById(UUID id);
    List<FanDevice> findAll();
    void deleteById(UUID id);
}
