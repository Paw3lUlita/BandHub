package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.shared.api.PageResponse;
import com.bandhub.zsi.ticketing.domain.TicketCode;
import com.bandhub.zsi.ticketing.dto.CreateTicketCodeRequest;
import com.bandhub.zsi.ticketing.dto.TicketCodeResponse;
import com.bandhub.zsi.ticketing.dto.UpdateTicketCodeRequest;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
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
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase();
        boolean descending = "desc".equalsIgnoreCase(sortDir);

        List<TicketCodeResponse> filtered = ticketCodeRepository.findAll().stream()
                .map(this::toResponse)
                .filter(code -> normalizedQuery.isBlank()
                        || code.codeValue().toLowerCase().contains(normalizedQuery)
                        || code.status().toLowerCase().contains(normalizedQuery)
                        || code.ticketId().toString().toLowerCase().contains(normalizedQuery))
                .sorted(resolveComparator(sortBy, descending))
                .toList();

        int safePage = Math.max(page, 0);
        int safeSize = Math.max(size, 1);
        int fromIndex = safePage * safeSize;
        int toIndex = Math.min(fromIndex + safeSize, filtered.size());
        List<TicketCodeResponse> content = fromIndex >= filtered.size()
                ? List.of()
                : filtered.subList(fromIndex, toIndex);

        return PageResponse.of(content, safePage, safeSize, filtered.size(), sortBy, sortDir, query);
    }

    private Comparator<TicketCodeResponse> resolveComparator(String sortBy, boolean descending) {
        Comparator<TicketCodeResponse> comparator = switch (sortBy) {
            case "status" -> Comparator.comparing(TicketCodeResponse::status, String.CASE_INSENSITIVE_ORDER);
            case "codeValue" -> Comparator.comparing(TicketCodeResponse::codeValue, String.CASE_INSENSITIVE_ORDER);
            case "generatedAt" -> Comparator.comparing(TicketCodeResponse::generatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(TicketCodeResponse::generatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        return descending ? comparator.reversed() : comparator;
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
