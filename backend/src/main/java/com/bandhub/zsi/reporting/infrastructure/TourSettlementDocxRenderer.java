package com.bandhub.zsi.reporting.infrastructure;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Podstawianie placeholderów w dokumencie Word (Apache POI XWPF). MVP: jeden run na paragraf po zamianie.
 * Publiczna, bo instancjonowana z {@link com.bandhub.zsi.reporting.BusinessReportService} (inny pakiet niż {@code infrastructure}).
 */
public class TourSettlementDocxRenderer {

    public byte[] render(InputStream template, Map<String, String> placeholders) {
        try (XWPFDocument doc = new XWPFDocument(template);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            for (XWPFParagraph p : doc.getParagraphs()) {
                replaceInParagraph(p, placeholders);
            }
            for (XWPFTable table : doc.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        for (XWPFParagraph p : cell.getParagraphs()) {
                            replaceInParagraph(p, placeholders);
                        }
                    }
                }
            }
            List<XWPFHeader> headers = doc.getHeaderList();
            if (headers != null) {
                for (XWPFHeader h : headers) {
                    for (XWPFParagraph p : h.getParagraphs()) {
                        replaceInParagraph(p, placeholders);
                    }
                }
            }
            doc.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("DOCX render failed", e);
        }
    }

    private void replaceInParagraph(XWPFParagraph p, Map<String, String> placeholders) {
        String text = p.getText();
        if (text == null || text.isBlank() || !text.contains("${")) {
            return;
        }
        String newText = text;
        for (var e : placeholders.entrySet()) {
            String v = e.getValue() != null ? e.getValue() : "";
            newText = newText.replace(e.getKey(), v);
        }
        if (newText.equals(text)) {
            return;
        }
        for (int i = p.getRuns().size() - 1; i >= 0; i--) {
            p.removeRun(i);
        }
        p.createRun().setText(newText);
    }
}
