package com.soodora.infrastructure.generator;

import com.soodora.domain.mediakit.MediaKitDTO;
import com.soodora.domain.mediakit.MediaKitGenerationException;
import com.soodora.domain.mediakititem.MediaKitItemDTO;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;

import lombok.extern.slf4j.Slf4j;

import static com.itextpdf.text.BaseColor.BLACK;
import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.NORMAL;
import static com.itextpdf.text.pdf.BaseFont.*;

@Slf4j
public class PdfMediaKitBuilder {

    private static final String OVERPASS_REGULAR = "service/src/main/resources/fonts/overpass/overpass-regular.otf";
    private static final String OVERPASS_SEMIBOLD = "service/src/main/resources/fonts/overpass/overpass-semibold.otf";

    private final Document document;
    private final Font titleFont;
    private final Font boldFont;
    private final Font normalFont;

    public PdfMediaKitBuilder(Document document) {
        this.document = document;

        titleFont = loadFont(OVERPASS_SEMIBOLD, 24, BOLD, BLACK);
        boldFont = loadFont(OVERPASS_SEMIBOLD, 14, BOLD, BLACK);
        normalFont = loadFont(OVERPASS_REGULAR, 12, NORMAL, BLACK);
    }

    public PdfMediaKitBuilder addBanner(String url) {
        try {
            Image logo = loadImage(url);
            Paragraph logoParagraph = new Paragraph();

            logo.scaleToFit(400, 100);
            logo.setAlignment(ALIGN_CENTER);

            logoParagraph.add(new Chunk(logo, 0, 0, true));
            logoParagraph.setAlignment(ALIGN_CENTER);
            logoParagraph.setSpacingAfter(20f);

            document.add(logoParagraph);
            return this;
        } catch (DocumentException exception) {
            log.error("Unable to append logo to the pdf: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to append logo");
        }
    }

    public PdfMediaKitBuilder addHeader(MediaKitDTO mediaKit) {
        String contactText = "Contact: " + mediaKit.contactEmail();

        if (!mediaKit.contactPhone().isBlank())
            contactText += " | " + mediaKit.contactPhone();
        try {
            document.add(new Paragraph(mediaKit.title(), titleFont));
            document.add(new Paragraph(mediaKit.artistName(), boldFont));
            document.add(new Paragraph(mediaKit.shortBio(), normalFont));
            document.add(new Paragraph(contactText, normalFont));
            return this;
        } catch (DocumentException exception) {
            log.error("Unable to append header to the pdf: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to append header");
        }
    }

    public PdfMediaKitBuilder addBiography(MediaKitDTO mediaKit)
            throws DocumentException {
        document.add(new Paragraph("\nBiography", boldFont));
        document.add(new Paragraph(mediaKit.longBio(), normalFont));
        return this;
    }

    public PdfMediaKitBuilder addMediaKitItems(Collection<MediaKitItemDTO> items) {
        for (MediaKitItemDTO item : items) {
            try {
                document.add(new Paragraph("\n" + item.title(), boldFont));
                document.add(new Paragraph("Type: " + item.type(), normalFont));

                switch (item.type().toLowerCase()) {
                    case "image" :
                        Image image = loadImage(item.content());

                        image.scaleToFit(500, 500);
                        document.add(image);
                        break;

                    case "video" :
                        URI uri = validateUrl(item.content());

                        document.add(new Paragraph("Video URL: " + uri, normalFont));
                        break;

                    default :
                        document.add(new Paragraph(item.content(), normalFont));
                        break;
                }
            } catch (DocumentException exception) {
                log.error("Unable to append media kit item to the pdf: {}", exception.getMessage());
                throw new MediaKitGenerationException("Unable to append media kit item");
            }
        }
        return this;
    }

    private URI validateUrl(String rawUrl) {
        try {
            return new URI(rawUrl);
        } catch (URISyntaxException exception) {
            throw new MediaKitGenerationException("Unable to parse url");
        }
    }

    private Font loadFont(String path, int size, int style, BaseColor color) {
        try {
            BaseFont baseFont = createFont(path, IDENTITY_H, EMBEDDED);

            return new Font(baseFont, size, style, color);
        } catch (IOException | DocumentException exception) {
            log.error("Unable to load font for pdf: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to load font");
        }
    }

    private Image loadImage(String url) {
        try {
            return Image.getInstance(validateUrl(url).toURL());
        } catch (BadElementException | IOException exception) {
            log.error("Unable to load image from url: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to load image from url");
        }
    }

}
