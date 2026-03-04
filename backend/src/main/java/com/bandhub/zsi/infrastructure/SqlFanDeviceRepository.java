package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.fan.FanDeviceRepository;
import com.bandhub.zsi.fan.domain.FanDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlFanDeviceRepository implements FanDeviceRepository {

    private final JpaFanDeviceRepository jpaRepository;

    SqlFanDeviceRepository(JpaFanDeviceRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public FanDevice save(FanDevice fanDevice) { return jpaRepository.save(fanDevice); }

    @Override
    public Optional<FanDevice> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<FanDevice> findAll() { return jpaRepository.findAll(); }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaFanDeviceRepository extends JpaRepository<FanDevice, UUID> {}
