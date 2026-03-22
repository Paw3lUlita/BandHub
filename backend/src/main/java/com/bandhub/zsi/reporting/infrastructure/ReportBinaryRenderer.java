package com.bandhub.zsi.reporting.infrastructure;

import com.bandhub.zsi.ecommerce.dto.MerchSalesSnapshotResponse;
import com.bandhub.zsi.logistics.dto.TourProfitabilityResponse;
import com.bandhub.zsi.ticketing.dto.TicketingEventSnapshotResponse;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Minimalny generator plików PDF (OpenPDF) i XLSX (Apache POI) dla raportów Sprintu 15.
 * Publiczny API dla {@link com.bandhub.zsi.reporting.BusinessReportService} (inny pakiet niż ten adapter).
 */
public class ReportBinaryRenderer {

    public byte[] merchPdf(MerchSalesSnapshotResponse r) {
        return simplePdf(
                "BandHub - raport sprzedazy merchu",
                new String[][]{
                        {"Liczba zamowien", String.valueOf(r.orderCount())},
                        {"Przychod lacznie", money(r.totalRevenue()) + " " + r.currency()},
                        {"Laczna liczba sztuk", String.valueOf(r.totalUnits())}
                }
        );
    }

    public byte[] merchXlsx(MerchSalesSnapshotResponse r) {
        return keyValueSheet(
                "Merch",
                new String[]{"Metryka", "Wartosc"},
                new String[][]{
                        {"Liczba zamowien", String.valueOf(r.orderCount())},
                        {"Przychod lacznie", money(r.totalRevenue())},
                        {"Waluta", r.currency()},
                        {"Laczna liczba sztuk", String.valueOf(r.totalUnits())}
                }
        );
    }

    public byte[] ticketingEventPdf(TicketingEventSnapshotResponse r) {
        return simplePdf(
                "BandHub - raport wydarzenia (ticketing)",
                new String[][]{
                        {"Koncert", Objects.toString(r.concertName(), "")},
                        {"Sprzedane bilety", String.valueOf(r.soldTickets())},
                        {"Pozostalo w pulach", String.valueOf(r.remainingTickets())},
                        {"Przychod", money(r.totalRevenue()) + " " + r.currency()},
                        {"Pojemnosc miejsca", String.valueOf(r.venueCapacity())},
                        {"Oblozenie %", String.format(java.util.Locale.US, "%.2f", r.occupancyPercent())}
                }
        );
    }

    public byte[] ticketingEventXlsx(TicketingEventSnapshotResponse r) {
        return keyValueSheet(
                "Ticketing",
                new String[]{"Metryka", "Wartosc"},
                new String[][]{
                        {"ID koncertu", r.concertId() != null ? r.concertId().toString() : ""},
                        {"Nazwa koncertu", Objects.toString(r.concertName(), "")},
                        {"Sprzedane bilety", String.valueOf(r.soldTickets())},
                        {"Pozostalo w pulach", String.valueOf(r.remainingTickets())},
                        {"Przychod", money(r.totalRevenue())},
                        {"Waluta", r.currency()},
                        {"Pojemnosc miejsca", String.valueOf(r.venueCapacity())},
                        {"Oblozenie %", String.format(java.util.Locale.US, "%.2f", r.occupancyPercent())}
                }
        );
    }

    public byte[] tourProfitabilityPdf(TourProfitabilityResponse r) {
        return simplePdf(
                "BandHub - rentownosc trasy",
                new String[][]{
                        {"Koszty lacznie", money(r.totalCosts()) + " " + r.currency()},
                        {"Przychod z biletow", money(r.ticketRevenue()) + " " + r.currency()},
                        {"Przychody reczne", money(r.manualRevenue()) + " " + r.currency()},
                        {"Przychod lacznie", money(r.totalRevenue()) + " " + r.currency()},
                        {"Bilans", money(r.balance()) + " " + r.currency()}
                }
        );
    }

    public byte[] tourProfitabilityXlsx(TourProfitabilityResponse r) {
        return keyValueSheet(
                "Trasa",
                new String[]{"Metryka", "Wartosc"},
                new String[][]{
                        {"Koszty lacznie", money(r.totalCosts())},
                        {"Przychod z biletow", money(r.ticketRevenue())},
                        {"Przychody reczne", money(r.manualRevenue())},
                        {"Przychod lacznie", money(r.totalRevenue())},
                        {"Bilans", money(r.balance())},
                        {"Waluta", r.currency()}
                }
        );
    }

    private static String money(BigDecimal v) {
        if (v == null) {
            return "0";
        }
        return v.toPlainString();
    }

    private static byte[] simplePdf(String title, String[][] rows) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph(title));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2f, 3f});
            for (String[] row : rows) {
                PdfPCell c0 = new PdfPCell(new Paragraph(row[0]));
                PdfPCell c1 = new PdfPCell(new Paragraph(row[1]));
                c0.setPadding(4f);
                c1.setPadding(4f);
                table.addCell(c0);
                table.addCell(c1);
            }
            document.add(table);
            document.close();
            return out.toByteArray();
        } catch (DocumentException | IOException e) {
            throw new IllegalStateException("PDF generation failed", e);
        }
    }

    private static byte[] keyValueSheet(String sheetName, String[] header, String[][] rows) {
        try (XSSFWorkbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(sheetName);
            Row h = sheet.createRow(0);
            h.createCell(0).setCellValue(header[0]);
            h.createCell(1).setCellValue(header[1]);
            int r = 1;
            for (String[] row : rows) {
                Row xr = sheet.createRow(r++);
                xr.createCell(0).setCellValue(row[0]);
                xr.createCell(1).setCellValue(row[1]);
            }
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            wb.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("XLSX generation failed", e);
        }
    }
}
