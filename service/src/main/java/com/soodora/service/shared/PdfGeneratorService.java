package com.soodora.service.shared;

import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.mediakititem.MediaKitItemDTO;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.SneakyThrows;

@Service
public class PdfGeneratorService {

    @SneakyThrows
    public ByteArrayOutputStream generateMediaKitPdf(MediaKitDTO mediaKit, List<MediaKitItemDTO> items) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
        Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

        document.add(new Paragraph(mediaKit.title(), titleFont));
        document.add(new Paragraph(mediaKit.shortBio(), normalFont));
        document.add(
                new Paragraph("Contact: " + mediaKit.contactEmail() + " | " + mediaKit.contactPhone(), normalFont));

        document.add(new Paragraph("\nBiography", boldFont));
        document.add(new Paragraph(mediaKit.longBio(), normalFont));

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

        document.close();
        return out;
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            throw new InvalidUrlException(e.getMessage());
        }
    }

}
