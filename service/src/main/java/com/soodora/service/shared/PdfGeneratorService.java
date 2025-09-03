package com.soodora.service.shared;

import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.mediakititem.MediaKitItemDTO;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import lombok.SneakyThrows;

@Service
public class PdfGeneratorService {

    private static final BaseColor ORANGE = new BaseColor(0xFF, 0x91, 0x4D);
    private static final BaseColor FLORAL_WHITE = new BaseColor(0xFF, 0xFA, 0xF0);
    private static final BaseColor BLACK = BaseColor.BLACK;
    private static final float HEADER_HEIGHT = 200f;

    private static final String LOGO_PATH = "service/src/main/resources/static/images/black_soodora_logo.png";
    private static final String OVERPASS_REGULAR = "service/src/main/resources/fonts/overpass/overpass-regular.otf";
    private static final String OVERPASS_SEMIBOLD = "service/src/main/resources/fonts/overpass/overpass-semibold.otf";

    @SneakyThrows
    public ByteArrayOutputStream generateMediaKitPdf(MediaKitDTO mediaKit, List<MediaKitItemDTO> items) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, out);

        writer.setPageEvent(new BackgroundPageEvent());

        document.open();

        Font titleFont = createFont(OVERPASS_SEMIBOLD, 24, Font.BOLD, BLACK);
        Font boldFont = createFont(OVERPASS_SEMIBOLD, 14, Font.BOLD, BLACK);
        Font normalFont = createFont(OVERPASS_REGULAR, 12, Font.NORMAL, BLACK);

        addLogo(document);
        addHeader(document, mediaKit, titleFont, boldFont, normalFont);
        addBiography(document, mediaKit, boldFont, normalFont);
        addMediaKitItems(document, items, boldFont, normalFont);

        document.close();
        return out;
    }

    private Font createFont(String path, int size, int style, BaseColor color) throws Exception {
        BaseFont baseFont = BaseFont.createFont(path, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        return new Font(baseFont, size, style, color);
    }

    @SneakyThrows
    private void addLogo(Document document) {
        try {
            Image logo = Image.getInstance(LOGO_PATH);
            logo.scaleToFit(400, 100);
            logo.setAlignment(Element.ALIGN_CENTER);

            Paragraph logoParagraph = new Paragraph();
            logoParagraph.add(new Chunk(logo, 0, 0, true));
            logoParagraph.setAlignment(Element.ALIGN_CENTER);
            logoParagraph.setSpacingAfter(20f);

            document.add(logoParagraph);
        } catch (Exception e) {
            System.out.println("Invalid Logo : " + e.getMessage());
        }
    }

    private void addHeader(Document document, MediaKitDTO mediaKit, Font titleFont, Font boldFont, Font normalFont)
            throws DocumentException {
        document.add(new Paragraph(mediaKit.title(), titleFont));
        document.add(new Paragraph(mediaKit.artistName(), boldFont));
        document.add(new Paragraph(mediaKit.shortBio(), normalFont));
        document.add(
                new Paragraph("Contact: " + mediaKit.contactEmail() + " | " + mediaKit.contactPhone(), normalFont));
    }

    private void addBiography(Document document, MediaKitDTO mediaKit, Font boldFont, Font normalFont)
            throws DocumentException {
        document.add(new Paragraph("\nBiography", boldFont));
        document.add(new Paragraph(mediaKit.longBio(), normalFont));
    }

    @SneakyThrows
    private void addMediaKitItems(Document document, List<MediaKitItemDTO> items, Font boldFont, Font normalFont)
            throws DocumentException {
        for (MediaKitItemDTO item : items) {
            document.add(new Paragraph("\n" + item.title(), boldFont));
            document.add(new Paragraph("Type: " + item.type(), normalFont));

            if ("image".equalsIgnoreCase(item.type()) && isValidUrl(item.content())) {
                Image img = Image.getInstance(new URL(item.content()));
                img.scaleToFit(500, 500);
                document.add(img);
            } else if ("video".equalsIgnoreCase(item.type()) && isValidUrl(item.content())) {
                document.add(new Paragraph("Video URL: " + item.content(), normalFont));
            } else {
                document.add(new Paragraph(item.content(), normalFont));
            }
        }
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            throw new InvalidUrlException(e.getMessage());
        }
    }

    static class BackgroundPageEvent extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = document.getPageSize();

            canvas.setColorFill(ORANGE);
            canvas.rectangle(rect.getLeft(), rect.getTop() - HEADER_HEIGHT, rect.getWidth(), HEADER_HEIGHT);
            canvas.fill();

            canvas.setColorFill(FLORAL_WHITE);
            canvas.rectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight() - HEADER_HEIGHT);
            canvas.fill();

            canvas.setColorStroke(BLACK);
            canvas.setLineWidth(2f);
            canvas.moveTo(rect.getLeft(), rect.getTop() - HEADER_HEIGHT);
            canvas.lineTo(rect.getRight(), rect.getTop() - HEADER_HEIGHT);
            canvas.stroke();
        }
    }

}
