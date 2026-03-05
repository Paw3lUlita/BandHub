package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.TicketCode;
import com.bandhub.zsi.ticketing.dto.CreateTicketCodeRequest;
import com.bandhub.zsi.ticketing.dto.TicketCodeResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketCodeRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TicketCodeAdminService {

    private final TicketCodeRepository ticketCodeRepository;

    public TicketCodeAdminService(TicketCodeRepository ticketCodeRepository) {
        this.ticketCodeRepository = ticketCodeRepository;
    }

    public UUID create(CreateTicketCodeRequest request) {
        TicketCode ticketCode = TicketCode.create(
                request.ticketId(),
                request.codeValue(),
                request.codeType(),
                request.status()
        );
        return ticketCodeRepository.save(ticketCode).getId();
    }

    public void update(UUID id, UpdateTicketCodeRequest request) {
        TicketCode ticketCode = ticketCodeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ticket code not found: " + id));
        ticketCode.update(
                request.codeValue(),
                request.codeType(),
                request.status(),
                request.activatedAt(),
                request.usedAt()
        );
    }

    public void delete(UUID id) {
        if (ticketCodeRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Ticket code not found: " + id);
        }
        ticketCodeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public TicketCodeResponse getOne(UUID id) {
        return ticketCodeRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Ticket code not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<TicketCodeResponse> getAll() {
        return ticketCodeRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PageResponse<TicketCodeResponse> getPage(int page, int size, String sortBy, String sortDir, String query) {
        var result = ticketCodeRepository.findPage(page, size, sortBy, sortDir, query);
        List<TicketCodeResponse> content = result.content().stream().map(this::toResponse).toList();
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        return PageResponse.of(content, safePage, safeSize, result.totalElements(), sortBy, sortDir, query);
    }

    private TicketCodeResponse toResponse(TicketCode code) {
        return new TicketCodeResponse(
                code.getId(),
                code.getTicketId(),
                code.getCodeValue(),
                code.getCodeType(),
                code.getStatus(),
                code.getGeneratedAt(),
                code.getActivatedAt(),
                code.getUsedAt()
        );
    }
}
