package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.TicketCode;
import com.bandhub.zsi.ticketing.domain.TicketValidation;
import com.bandhub.zsi.ticketing.dto.CreateTicketValidationRequest;
import com.bandhub.zsi.ticketing.dto.TicketValidationResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketValidationRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketValidationAdminService {

    private final TicketValidationRepository ticketValidationRepository;
    private final TicketCodeRepository ticketCodeRepository;

    public TicketValidationAdminService(TicketValidationRepository ticketValidationRepository, TicketCodeRepository ticketCodeRepository) {
        this.ticketValidationRepository = ticketValidationRepository;
        this.ticketCodeRepository = ticketCodeRepository;
    }

    public UUID create(CreateTicketValidationRequest request) {
        TicketCode code = ticketCodeRepository.findById(request.ticketCodeId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket code not found: " + request.ticketCodeId()));
        TicketValidation validation = TicketValidation.create(code, request.validatedBy(), request.gateName(), request.validationResult(), request.details());
        return ticketValidationRepository.save(validation).getId();
    }

    public void update(UUID id, UpdateTicketValidationRequest request) {
        TicketValidation validation = ticketValidationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket validation not found: " + id));
        TicketCode code = ticketCodeRepository.findById(request.ticketCodeId())
                .orElseThrow(() -> new EntityNotFoundException("Ticket code not found: " + request.ticketCodeId()));
        validation.update(code, request.validatedBy(), request.gateName(), request.validationResult(), request.validationTime(), request.details());
    }

    public void delete(UUID id) {
        if (ticketValidationRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Ticket validation not found: " + id);
        }
        ticketValidationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketValidationResponse getOne(UUID id) {
        return ticketValidationRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket validation not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TicketValidationResponse> getAll() {
        return ticketValidationRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketValidationResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = ticketValidationRepository.findPage(page, size, sortBy, sortDir, query);
        List<TicketValidationResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private TicketValidationResponse toResponse(TicketValidation validation) {
        return new TicketValidationResponse(
                validation.getId(),
                validation.getTicketCode().getId(),
                validation.getValidatedBy(),
                validation.getGateName(),
                validation.getValidationResult(),
                validation.getValidationTime(),
                validation.getDetails()
        );
    }
}
