package com.bandhub.zsi.reporting.infrastructure;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Scenariusz: zmiana treści dokumentu (szablonu) bez zmiany kodu — silnik tylko podstawia placeholdery.
 */
class TourSettlementDocxRendererTest {

    @Test
    void replacesPlaceholdersWithoutCodeChange() throws Exception {
        TourSettlementDocxRenderer renderer = new TourSettlementDocxRenderer();
        byte[] templateBytes;
        try (XWPFDocument doc = new XWPFDocument()) {
            XWPFParagraph p = doc.createParagraph();
            p.createRun().setText("Trasa: ${tourName} | bilans: ${profitBalance}");
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            doc.write(bos);
            templateBytes = bos.toByteArray();
        }

        byte[] out = renderer.render(
                new ByteArrayInputStream(templateBytes),
                Map.of("${tourName}", "Europa 2025", "${profitBalance}", "1234.56")
        );

        try (XWPFDocument result = new XWPFDocument(new ByteArrayInputStream(out))) {
            String text = result.getParagraphs().get(0).getText();
            assertTrue(text.contains("Europa 2025"));
            assertTrue(text.contains("1234.56"));
            assertTrue(!text.contains("${tourName}"));
        }
    }
}
