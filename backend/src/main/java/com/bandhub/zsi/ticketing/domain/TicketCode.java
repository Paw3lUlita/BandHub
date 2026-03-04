package com.bandhub.zsi.ticketing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketCode {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "ticket_id")
    private UUID ticketId;

    @Column(name = "code_value")
    private String codeValue;

    @Column(name = "code_type")
    private String codeType;

    private String status;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    public static TicketCode create(UUID ticketId, String codeValue, String codeType, String status) {
        TicketCode code = new TicketCode();
        code.ticketId = ticketId;
        code.codeValue = codeValue;
        code.codeType = codeType == null || codeType.isBlank() ? "QR" : codeType;
        code.status = status == null || status.isBlank() ? "ACTIVE" : status;
        code.generatedAt = LocalDateTime.now();
        return code;
    }

    public void update(String codeValue, String codeType, String status, LocalDateTime activatedAt, LocalDateTime usedAt) {
        this.codeValue = codeValue;
        this.codeType = codeType;
        this.status = status;
        this.activatedAt = activatedAt;
        this.usedAt = usedAt;
    }
}
