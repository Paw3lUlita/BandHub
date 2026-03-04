package com.bandhub.zsi.infrastructure;

import com.bandhub.zsi.ticketing.TicketValidationRepository;
import com.bandhub.zsi.ticketing.domain.TicketValidation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class SqlTicketValidationRepository implements TicketValidationRepository {

    private final JpaTicketValidationRepository jpaRepository;

    SqlTicketValidationRepository(JpaTicketValidationRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TicketValidation save(TicketValidation ticketValidation) { return jpaRepository.save(ticketValidation); }

    @Override
    public Optional<TicketValidation> findById(UUID id) { return jpaRepository.findById(id); }

    @Override
    public List<TicketValidation> findAll() { return jpaRepository.findAll(); }

    @Override
    public void deleteById(UUID id) { jpaRepository.deleteById(id); }
}

interface JpaTicketValidationRepository extends JpaRepository<TicketValidation, UUID> {}
