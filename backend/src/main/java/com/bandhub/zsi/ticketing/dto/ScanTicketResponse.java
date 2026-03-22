package com.bandhub.zsi.ticketing.dto;

public record ScanTicketResponse(
        boolean valid,
        String result,
        String message,
        String concertName,
        String poolName,
        String codeValue
) {

    public static ScanTicketResponse success(String message, String concertName, String poolName, String codeValue) {
        return new ScanTicketResponse(true, "SUCCESS", message, concertName, poolName, codeValue);
    }

    public static ScanTicketResponse denied(String result, String message, String concertName, String poolName, String codeValue) {
        return new ScanTicketResponse(false, result, message, concertName, poolName, codeValue);
    }
}
