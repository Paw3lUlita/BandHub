package com.bandhub.zsi.ticketing;

import com.bandhub.zsi.ticketing.domain.TicketCode;
import com.bandhub.zsi.ticketing.domain.TicketValidation;
import com.bandhub.zsi.ticketing.dto.ScanTicketRequest;
import com.bandhub.zsi.ticketing.dto.ScanTicketResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketScanAdminService {

    private final TicketCodeRepository ticketCodeRepository;
    private final TicketValidationRepository ticketValidationRepository;
    private final TicketRepository ticketRepository;

    public TicketScanAdminService(
            TicketCodeRepository ticketCodeRepository,
            TicketValidationRepository ticketValidationRepository,
            TicketRepository ticketRepository
    ) {
        this.ticketCodeRepository = ticketCodeRepository;
        this.ticketValidationRepository = ticketValidationRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public ScanTicketResponse scan(ScanTicketRequest request, String validatedBy) {
        String raw = request.codeValue() == null ? "" : request.codeValue().trim();
        if (raw.isEmpty()) {
            return ScanTicketResponse.denied("EMPTY", "Brak kodu", null, null, "");
        }
        String gate = request.gateName() != null && !request.gateName().isBlank() ? request.gateName().trim() : "MAIN";

        return ticketCodeRepository.findByCodeValue(raw)
                .map(code -> handleExistingCode(code, validatedBy, gate, raw))
                .orElseGet(() -> ScanTicketResponse.denied("UNKNOWN", "Nie znaleziono kodu", null, null, raw));
    }

    private ScanTicketResponse handleExistingCode(TicketCode code, String validatedBy, String gate, String raw) {
        String status = code.getStatus() != null ? code.getStatus().toUpperCase() : "";
        if ("CANCELLED".equals(status)) {
            ticketValidationRepository.save(TicketValidation.create(
                    code, validatedBy, gate, "DENIED", "Bilet anulowany"
            ));
            return ScanTicketResponse.denied("CANCELLED", "Bilet anulowany", null, null, raw);
        }
        if ("USED".equals(status)) {
            ticketValidationRepository.save(TicketValidation.create(
                    code, validatedBy, gate, "DENIED", "Bilet już wykorzystany"
            ));
            return ScanTicketResponse.denied("USED", "Bilet już wykorzystany", null, null, raw);
        }
        if (!code.isActive()) {
            ticketValidationRepository.save(TicketValidation.create(
                    code, validatedBy, gate, "DENIED", "Nieprawidłowy status biletu: " + code.getStatus()
            ));
            return ScanTicketResponse.denied("INVALID_STATUS", "Nieprawidłowy status biletu", null, null, raw);
        }
        try {
            code.markUsed();
            ticketCodeRepository.save(code);
            ticketValidationRepository.save(TicketValidation.create(
                    code, validatedBy, gate, "SUCCESS", null
            ));
            var ctx = resolveContext(code);
            return ScanTicketResponse.success(
                    "Wejście zarejestrowane",
                    ctx.concertName(),
                    ctx.poolName(),
                    raw
            );
        } catch (IllegalStateException ex) {
            ticketValidationRepository.save(TicketValidation.create(
                    code, validatedBy, gate, "DENIED", ex.getMessage()
            ));
            return ScanTicketResponse.denied("DENIED", ex.getMessage(), null, null, raw);
        }
    }

    private Context resolveContext(TicketCode code) {
        return ticketRepository.findById(code.getTicketId())
                .map(ticket -> {
                    var pool = ticket.getTicketPool();
                    var concert = pool.getConcert();
                    return new Context(
                            concert != null ? concert.getName() : null,
                            pool.getName()
                    );
                })
                .orElse(new Context(null, null));
    }

    private record Context(String concertName, String poolName) {
    }
}
