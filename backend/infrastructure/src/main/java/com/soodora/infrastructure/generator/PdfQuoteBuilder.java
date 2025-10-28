package com.soodora.infrastructure.generator;

import com.soodora.domain.common.Money;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quotearticle.QuoteArticleDTO;

import java.net.URI;
import java.util.List;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PdfQuoteBuilder extends LoadFontFromResources {

    private static final String OVERPASS_REGULAR = "/fonts/overpass/overpass-regular.otf";
    private static final String OVERPASS_SEMIBOLD = "/fonts/overpass/overpass-semibold.otf";

    private final Document document;
    private final Font titleFont;
    private final Font boldFont;
    private final Font normalFont;

    public PdfQuoteBuilder(Document document) {
        this.document = document;
        titleFont = loadFontFromResources(OVERPASS_SEMIBOLD, 24, Font.BOLD, BaseColor.BLACK);
        boldFont = loadFontFromResources(OVERPASS_SEMIBOLD, 14, Font.BOLD, BaseColor.BLACK);
        normalFont = loadFontFromResources(OVERPASS_REGULAR, 12, Font.NORMAL, BaseColor.BLACK);
    }

    public PdfQuoteBuilder addBanner(String url) {
        try {
            Image logo = Image.getInstance(new URI(url).toURL());
            logo.scaleToFit(400, 100);
            logo.setAlignment(Element.ALIGN_CENTER);
            Paragraph paragraph = new Paragraph();
            paragraph.add(new Chunk(logo, 0, 0, true));
            paragraph.setAlignment(Element.ALIGN_CENTER);
            paragraph.setSpacingAfter(20f);
            document.add(paragraph);
        } catch (Exception e) {
            log.error("Unable to append logo: {}", e.getMessage());
        }
        return this;
    }

    public PdfQuoteBuilder addHeader(QuoteDTO quote) throws DocumentException {
        Paragraph title = new Paragraph("Quote", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10f);

        Paragraph date = new Paragraph(
                new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(quote.createdAtDate()),
                boldFont);

        Paragraph clientInfo = new Paragraph(
                "Client: " + quote.clientName() + " | " + quote.clientEmail() +
                        (quote.clientPhone() != null
                                ? " | " + quote.clientPhone() + " | " + quote.clientAddress()
                                : ""),
                normalFont);
        clientInfo.setSpacingAfter(20f);

        Paragraph artistInfo = new Paragraph(
                quote.artist().username(), boldFont);
        artistInfo.setSpacingAfter(10f);

        document.add(title);
        document.add(date);
        document.add(clientInfo);
        document.add(artistInfo);

        return this;
    }

    public PdfQuoteBuilder addQuoteTable(List<QuoteArticleDTO> articles, Money total) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[] {2, 4, 1, 2, 2, 2});

        addCell(table, "Article", boldFont);
        addCell(table, "Description", boldFont);
        addCell(table, "Qty", boldFont);
        addCell(table, "Unit Price", boldFont);
        addCell(table, "VAT %", boldFont);
        addCell(table, "Total", boldFont);

        for (QuoteArticleDTO article : articles) {
            addCell(table, article.slug(), normalFont);
            addCell(table, article.description(), normalFont);
            addCell(table, String.valueOf(article.quantity()), normalFont);
            addCell(table, formatMoney(article.unitPrice()), normalFont);
            addCell(table, article.vatPercent().toPlainString() + "%", normalFont);
            addCell(table, formatMoney(article.totalPrice()), normalFont);
        }

        PdfPCell totalCell = new PdfPCell(new Phrase("Total", boldFont));
        totalCell.setColspan(5);
        totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(totalCell);
        addCell(table, formatMoney(total), boldFont);

        document.add(table);
        return this;
    }

    private String formatMoney(Money money) {
        return money.amount() + " " + money.currency().getSymbol();
    }

    private void addCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

}
