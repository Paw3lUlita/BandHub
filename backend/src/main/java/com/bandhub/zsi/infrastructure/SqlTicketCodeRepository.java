package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketCodeRepository;
import com.bandhub.zsi.ticketing.domain.TicketCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketCodeRepository implements TicketCodeRepository {

    private final JpaTicketCodeRepository jpaRepository;

    SqlTicketCodeRepository(JpaTicketCodeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketCode save(TicketCode ticketCode) {
        return jpaRepository.save(ticketCode);
    }

    @Override
    public Optional<TicketCode> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<TicketCode> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}

interface JpaTicketCodeRepository extends JpaRepository<TicketCode, UUID> {}
