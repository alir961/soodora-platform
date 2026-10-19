package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.Money;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.generator.PdfExporter;
import com.soodora.domain.quote.*;
import com.soodora.domain.quotearticle.CreateQuoteArticleCommand;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.quotearticle.QuoteArticleRepository;
import com.soodora.domain.quotearticle.UpdateQuoteArticleCommand;
import com.soodora.domain.user.User;
import com.soodora.service.shared.InvalidPermissionException;
import com.soodora.test.factory.QuoteArticleFactory;
import com.soodora.test.factory.QuoteFactory;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class QuoteServiceTest {

    // Default quotes and articles belong to the artist built by QuoteFactory, under
    // the "quoteSlug" slug
    private static final QuoteDTO QUOTE = QuoteFactory.buildDefaultQuote("");
    private static final QuoteArticleDTO ARTICLE = QuoteArticleFactory.buildDefaultQuoteArticle();
    private static final Pageable PAGEABLE = PageRequest.of(0, 50);

    private static final CreateQuoteCommand CREATE_COMMAND = new CreateQuoteCommand(QUOTE.name(), QUOTE.clientName(),
            QUOTE.clientEmail(), QUOTE.clientAddress(), QUOTE.clientPhone(), QUOTE.description());
    private static final UpdateQuoteCommand UPDATE_COMMAND = new UpdateQuoteCommand("Updated Name", "Updated Client",
            "client@example.com", "123 Street", "1234567890", "Updated description");
    private static final CreateQuoteArticleCommand CREATE_ARTICLE_COMMAND = new CreateQuoteArticleCommand(
            ARTICLE.quoteSlug(), ARTICLE.description(), ARTICLE.quantity(), ARTICLE.unitPrice().amount(),
            ARTICLE.unitPrice().currency(), ARTICLE.vatPercent());
    private static final UpdateQuoteArticleCommand UPDATE_ARTICLE_COMMAND = new UpdateQuoteArticleCommand(
            "Updated Article Description", 10, BigDecimal.valueOf(149.99), Currency.USD, BigDecimal.valueOf(25));

    private QuoteRepository quoteRepository;
    private QuoteArticleRepository quoteArticleRepository;
    private PdfExporter pdfExporter;
    private QuoteService quoteService;

    private final User owner = user(QUOTE.artist().slug(), false);
    private final User intruder = user("another-artist", false);
    private final User admin = user("admin", true);

    private static User user(String slug, boolean isAdmin) {
        User user = mock(User.class);
        when(user.getSlug()).thenReturn(slug);
        when(user.isAdmin()).thenReturn(isAdmin);
        return user;
    }

    @BeforeEach
    void setUp() {
        quoteRepository = mock(QuoteRepository.class);
        quoteArticleRepository = mock(QuoteArticleRepository.class);
        pdfExporter = mock(PdfExporter.class);
        quoteService = new QuoteService(quoteRepository, quoteArticleRepository, pdfExporter);
        when(quoteRepository.findBySlug(QUOTE.slug())).thenReturn(QUOTE);
        when(quoteArticleRepository.findBySlug(ARTICLE.slug())).thenReturn(ARTICLE);
    }

    // ── Quotes ──────────────────────────────────────────────────────────────────

    @Test
    void ownerCanReadTheirQuote() {
        assertEquals(QUOTE, quoteService.getQuote(QUOTE.slug(), owner));
    }

    @Test
    void adminCanReadAnyQuote() {
        assertEquals(QUOTE, quoteService.getQuote(QUOTE.slug(), admin));
    }

    @Test
    void anotherArtistCannotReadTheQuote() {
        assertThrows(InvalidPermissionException.class, () -> quoteService.getQuote(QUOTE.slug(), intruder));
    }

    @Test
    void unknownQuoteIsNotFound() {
        when(quoteRepository.findBySlug("unknown")).thenThrow(UnknownSlugException.class);

        assertThrows(UnknownQuoteException.class, () -> quoteService.getQuote("unknown", owner));
    }

    @Test
    void createQuoteSavesForTheUser() {
        when(quoteRepository.isQuoteNameUsedByUser(CREATE_COMMAND.name(), owner.getSlug())).thenReturn(false);

        assertDoesNotThrow(() -> quoteService.createQuote(CREATE_COMMAND, owner));
        verify(quoteRepository).save(CREATE_COMMAND, owner);
    }

    @Test
    void createQuoteRejectsADuplicateName() {
        when(quoteRepository.isQuoteNameUsedByUser(CREATE_COMMAND.name(), owner.getSlug())).thenReturn(true);

        assertThrows(QuoteUnityException.class, () -> quoteService.createQuote(CREATE_COMMAND, owner));
    }

    @Test
    void ownerCanUpdateTheirQuote() {
        when(quoteRepository.update(UPDATE_COMMAND, QUOTE.slug())).thenReturn(QUOTE);

        assertEquals(QUOTE, quoteService.updateQuote(UPDATE_COMMAND, QUOTE.slug(), owner));
    }

    @Test
    void anotherArtistCannotUpdateTheQuote() {
        assertThrows(InvalidPermissionException.class,
                () -> quoteService.updateQuote(UPDATE_COMMAND, QUOTE.slug(), intruder));
        verify(quoteRepository, never()).update(any(), anyString());
    }

    @Test
    void ownerCanDeleteTheirQuote() {
        quoteService.deleteQuote(QUOTE.slug(), owner);

        verify(quoteRepository).deleteQuoteBySlug(QUOTE.slug());
    }

    @Test
    void anotherArtistCannotDeleteTheQuote() {
        assertThrows(InvalidPermissionException.class, () -> quoteService.deleteQuote(QUOTE.slug(), intruder));
        verify(quoteRepository, never()).deleteQuoteBySlug(anyString());
    }

    @Test
    void searchIsScopedToTheArtistWhateverUserSlugIsSent() {
        QuoteFilter requested = new QuoteFilter(of("another-artist"), of("Sample"), empty(), empty());
        QuoteFilter enforced = new QuoteFilter(of(owner.getSlug()), of("Sample"), empty(), empty());
        Page<QuoteDTO> page = new PageImpl<>(List.of(QUOTE));
        when(quoteRepository.findByFilter(enforced, PAGEABLE)).thenReturn(page);

        assertEquals(page, quoteService.getQuotesByFilter(requested, owner, PAGEABLE));
        verify(quoteRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void adminSearchWithoutFilterListsEverything() {
        Page<QuoteDTO> page = new PageImpl<>(List.of(QUOTE));
        when(quoteRepository.findAll(PAGEABLE)).thenReturn(page);

        assertEquals(page, quoteService.getQuotesByFilter(new QuoteFilter(empty(), empty(), empty(), empty()), admin,
                PAGEABLE));
    }

    // ── Articles ────────────────────────────────────────────────────────────────

    @Test
    void ownerCanReadAndAddArticles() {
        when(quoteArticleRepository.save(CREATE_ARTICLE_COMMAND)).thenReturn(ARTICLE);

        assertEquals(ARTICLE, quoteService.getQuoteArticle(ARTICLE.slug(), owner));
        assertEquals(ARTICLE, quoteService.addQuoteArticle(CREATE_ARTICLE_COMMAND, owner));
    }

    @Test
    void anotherArtistCannotTouchArticles() {
        assertThrows(InvalidPermissionException.class, () -> quoteService.getQuoteArticle(ARTICLE.slug(), intruder));
        assertThrows(InvalidPermissionException.class,
                () -> quoteService.addQuoteArticle(CREATE_ARTICLE_COMMAND, intruder));
        assertThrows(InvalidPermissionException.class,
                () -> quoteService.updateQuoteArticle(UPDATE_ARTICLE_COMMAND, ARTICLE.slug(), intruder));
        assertThrows(InvalidPermissionException.class,
                () -> quoteService.deleteQuoteArticle(ARTICLE.slug(), intruder));
        verify(quoteArticleRepository, never()).save(any());
        verify(quoteArticleRepository, never()).update(any(), anyString());
        verify(quoteArticleRepository, never()).deleteArticleQuoteBySlug(anyString());
    }

    @Test
    void ownerCanUpdateAndDeleteArticles() {
        when(quoteArticleRepository.update(UPDATE_ARTICLE_COMMAND, ARTICLE.slug())).thenReturn(ARTICLE);

        assertEquals(ARTICLE, quoteService.updateQuoteArticle(UPDATE_ARTICLE_COMMAND, ARTICLE.slug(), owner));
        quoteService.deleteQuoteArticle(ARTICLE.slug(), owner);
        verify(quoteArticleRepository).deleteArticleQuoteBySlug(ARTICLE.slug());
    }

    @Test
    void unknownArticleIsNotFound() {
        when(quoteArticleRepository.findBySlug("unknown")).thenThrow(UnknownSlugException.class);

        assertThrows(UnknownQuoteArticleException.class, () -> quoteService.getQuoteArticle("unknown", owner));
    }

    @Test
    void articleListOnlyContainsTheArtistsOwnArticles() {
        QuoteArticleDTO foreign = new QuoteArticleDTO("someone-elses-quote", "foreign", "x", 1,
                Money.of(BigDecimal.ONE, Currency.EUR), BigDecimal.ZERO, Money.of(BigDecimal.ONE, Currency.EUR));
        when(quoteRepository.findByFilter(any(), any())).thenReturn(new PageImpl<>(List.of(QUOTE)));
        when(quoteArticleRepository.findAll()).thenReturn(List.of(ARTICLE, foreign));

        assertEquals(List.of(ARTICLE), quoteService.getAllQuoteArticles(owner, PAGEABLE).getContent());
    }

    // ── PDF export ──────────────────────────────────────────────────────────────

    @Test
    void ownerCanExportTheQuote() throws Exception {
        QuoteArticleDTO article = new QuoteArticleDTO(QUOTE.slug(), "a", "d", 1, Money.of(BigDecimal.ONE, Currency.EUR),
                BigDecimal.ZERO, Money.of(BigDecimal.ONE, Currency.EUR));
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        pdf.write("PDF".getBytes());
        when(quoteArticleRepository.findAll()).thenReturn(List.of(article));
        when(pdfExporter.exportQuote(QUOTE, List.of(article))).thenReturn(pdf);

        assertArrayEquals("PDF".getBytes(), quoteService.exportQuoteToPdf(QUOTE.slug(), owner));
    }

    @Test
    void anotherArtistCannotExportTheQuote() {
        assertThrows(InvalidPermissionException.class, () -> quoteService.exportQuoteToPdf(QUOTE.slug(), intruder));
        verifyNoInteractions(pdfExporter);
    }
}
