package com.soodora.infrastructure.generator;

import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.mediakit.MediaKit;
import com.soodora.domain.mediakit.MediaKitGenerationException;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quote.QuoteGenerationException;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.stereotype.Component;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfExporterImpl implements PdfExporter {

    private static final BaseColor ORANGE = new BaseColor(0xFF, 0x91, 0x4D);
    private static final BaseColor FLORAL_WHITE = new BaseColor(0xFF, 0xFA, 0xF0);
    private static final BaseColor BLACK = BaseColor.BLACK;
    private static final float HEADER_HEIGHT = 200f;

    private static final String LOGO_URL = "https://app.soodora.com/assets/images/png/banner-dark.png";

    @Override
    public ByteArrayOutputStream exportMediaKit(MediaKit mediaKit)
            throws MediaKitGenerationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);

            writer.setPageEvent(new BackgroundPageEvent());
            document.open();

            new PdfMediaKitBuilder(document)
                    .addBanner(LOGO_URL)
                    .addHeader(mediaKit)
                    .addBiography(mediaKit)
                    .addMediaKitItems(mediaKit.getItems());

            document.close();
        } catch (DocumentException exception) {
            log.error("Unable to write a pdf: {}", exception.getMessage());
            throw new MediaKitGenerationException("Unable to write a pdf");
        }
        return out;
    }

    @Override
    public ByteArrayOutputStream exportQuote(QuoteDTO quote, List<QuoteArticleDTO> quoteArticles)
            throws QuoteGenerationException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new BackgroundPageEvent());
            document.open();

            new PdfQuoteBuilder(document)
                    .addBanner(LOGO_URL)
                    .addHeader(quote)
                    .addQuoteTable(quoteArticles, quote.total());

            document.close();
        } catch (DocumentException exception) {
            log.error("Unable to write a pdf: {}", exception.getMessage());
            throw new QuoteGenerationException("Unable to write a pdf");
        }

        return out;
    }

    static class BackgroundPageEvent extends PdfPageEventHelper {

        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle pageSize = document.getPageSize();

            canvas.setColorFill(ORANGE);
            canvas.rectangle(pageSize.getLeft(), pageSize.getTop() - HEADER_HEIGHT, pageSize.getWidth(), HEADER_HEIGHT);
            canvas.fill();

            canvas.setColorFill(FLORAL_WHITE);
            canvas.rectangle(pageSize.getLeft(), pageSize.getBottom(), pageSize.getWidth(),
                    pageSize.getHeight() - HEADER_HEIGHT);
            canvas.fill();

            canvas.setColorStroke(BLACK);
            canvas.setLineWidth(2f);
            canvas.moveTo(pageSize.getLeft(), pageSize.getTop() - HEADER_HEIGHT);
            canvas.lineTo(pageSize.getRight(), pageSize.getTop() - HEADER_HEIGHT);
            canvas.stroke();
        }

    }

}
