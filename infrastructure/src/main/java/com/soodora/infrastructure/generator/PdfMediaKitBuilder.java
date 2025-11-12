package com.soodora.infrastructure.generator;

import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakit.MediaKitGenerationException;
import com.soodora.domain.mediakititem.MediaKitItem;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

import com.itextpdf.text.*;

import lombok.extern.slf4j.Slf4j;

import static com.itextpdf.text.BaseColor.BLACK;
import static com.itextpdf.text.Element.ALIGN_CENTER;
import static com.itextpdf.text.Font.BOLD;
import static com.itextpdf.text.Font.NORMAL;

@Slf4j
public class PdfMediaKitBuilder extends LoadFontFromResources {

    private static final String OVERPASS_REGULAR = "/fonts/overpass/overpass-regular.otf";
    private static final String OVERPASS_SEMIBOLD = "/fonts/overpass/overpass-semibold.otf";

    private final Document document;
    private final Font titleFont;
    private final Font boldFont;
    private final Font normalFont;

    public PdfMediaKitBuilder(Document document) {
        this.document = document;

        titleFont = loadFontFromResources(OVERPASS_SEMIBOLD, 24, BOLD, BLACK);
        boldFont = loadFontFromResources(OVERPASS_SEMIBOLD, 14, BOLD, BLACK);
        normalFont = loadFontFromResources(OVERPASS_REGULAR, 12, NORMAL, BLACK);
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

    public PdfMediaKitBuilder addHeader(MediaKit mediaKit) {
        String contactText = "Contact: " + mediaKit.getContactEmail();

        if (!mediaKit.getContactPhone().isBlank())
            contactText += " | " + mediaKit.getContactPhone();
        try {
            document.add(new Paragraph(mediaKit.getTitle(), titleFont));
            document.add(new Paragraph(mediaKit.getOwner().getUsername().value(), boldFont));
            document.add(new Paragraph(mediaKit.getShortBio(), normalFont));
            document.add(new Paragraph(contactText, normalFont));
            return this;
        } catch (DocumentException exception) {
            log.error("Unable to append header to the pdf: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to append header");
        }
    }

    public PdfMediaKitBuilder addBiography(MediaKit mediaKit)
            throws DocumentException {
        document.add(new Paragraph("\nBiography", boldFont));
        document.add(new Paragraph(mediaKit.getLongBio(), normalFont));
        return this;
    }

    public PdfMediaKitBuilder addMediaKitItems(Collection<MediaKitItem> items) {
        for (MediaKitItem item : items) {
            try {
                document.add(new Paragraph("\n" + item.getTitle(), boldFont));
                document.add(new Paragraph("Type: " + item.getType(), normalFont));

                switch (item.getType().toLowerCase()) {
                    case "image" :
                        Image image = loadImage(item.getContent());

                        image.scaleToFit(500, 500);
                        document.add(image);
                        break;

                    case "video" :
                        URI uri = validateUrl(item.getContent());

                        document.add(new Paragraph("Video URL: " + uri, normalFont));
                        break;

                    default :
                        document.add(new Paragraph(item.getContent(), normalFont));
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

    private Image loadImage(String url) {
        try {
            return Image.getInstance(validateUrl(url).toURL());
        } catch (BadElementException | IOException exception) {
            log.error("Unable to load image from url: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to load image from url");
        }
    }

}
