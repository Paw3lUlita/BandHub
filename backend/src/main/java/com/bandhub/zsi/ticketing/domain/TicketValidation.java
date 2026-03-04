package com.bandhub.zsi.ticketing.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ticket_validations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketValidation {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_code_id", nullable = false)
    private TicketCode ticketCode;

    @Column(name = "validated_by")
    private String validatedBy;

    @Column(name = "gate_name")
    private String gateName;

    @Column(name = "validation_result")
    private String validationResult;

    @Column(name = "validation_time")
    private LocalDateTime validationTime;

    private String details;

    public static TicketValidation create(TicketCode ticketCode, String validatedBy, String gateName, String validationResult, String details) {
        TicketValidation validation = new TicketValidation();
        validation.ticketCode = ticketCode;
        validation.validatedBy = validatedBy;
        validation.gateName = gateName;
        validation.validationResult = validationResult;
        validation.validationTime = LocalDateTime.now();
        validation.details = details;
        return validation;
    }

    public void update(TicketCode ticketCode, String validatedBy, String gateName, String validationResult, LocalDateTime validationTime, String details) {
        this.ticketCode = ticketCode;
        this.validatedBy = validatedBy;
        this.gateName = gateName;
        this.validationResult = validationResult;
        this.validationTime = validationTime;
        this.details = details;
    }
}
