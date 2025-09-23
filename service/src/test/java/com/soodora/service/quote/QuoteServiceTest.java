package com.soodora.service.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.quote.*;
import com.soodora.domain.quotearticle.*;
import com.soodora.test.factory.QuoteArticleFactory;
import com.soodora.test.factory.QuoteFactory;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.test.TestConstants.USER;

@ExtendWith(MockitoExtension.class)
class QuoteServiceTest {

    @Mock
    private QuoteRepository quoteRepository;

    @Mock
    private QuoteArticleRepository quoteArticleRepository;

    private QuoteService quoteService;

    private static final QuoteDTO QUOTE_1 = QuoteFactory.buildDefaultQuote("1");
    private static final QuoteDTO QUOTE_2 = QuoteFactory.buildDefaultQuote("2");
    private static final QuoteDTO QUOTE_3 = QuoteFactory.buildDefaultQuote("3");
    private static final QuoteArticleDTO QUOTE_ARTICLE = QuoteArticleFactory.buildDefaultQuoteArticle();
    private static final String SLUG_1 = "slug1";
    private static final Pageable PAGEABLE = PageRequest.of(0, 50, Sort.by("slug").ascending());
    private static final List<QuoteDTO> QUOTES = List.of(QUOTE_1, QUOTE_2, QUOTE_3);
    private static final Page<QuoteDTO> QUOTES_PAGE = new PageImpl<>(QUOTES, PAGEABLE, QUOTES.size());
    private static final CreateQuoteCommand CREATE_COMMAND = new CreateQuoteCommand(
            QUOTE_1.name(),
            QUOTE_1.clientName(),
            QUOTE_1.clientEmail(),
            QUOTE_1.clientAddress(),
            QUOTE_1.clientPhone(),
            QUOTE_1.description());
    private static final UpdateQuoteCommand UPDATE_QUOTE_COMMAND = new UpdateQuoteCommand(
            "Updated Name",
            "Updated Client",
            "client@example.com",
            "123 Street",
            "1234567890",
            "Updated description");
    private static final CreateQuoteArticleCommand CREATE_QUOTE_ARTICLE_COMMAND = new CreateQuoteArticleCommand(
            QUOTE_ARTICLE.quoteSlug(),
            QUOTE_ARTICLE.description(),
            QUOTE_ARTICLE.quantity(),
            QUOTE_ARTICLE.unitPrice().amount().doubleValue(),
            QUOTE_ARTICLE.unitPrice().currency(),
            QUOTE_ARTICLE.vatPercent());
    private static final UpdateQuoteArticleCommand UPDATE_QUOTE_ARTICLE_COMMAND = new UpdateQuoteArticleCommand(
            "Updated Article Description",
            10,
            149.99,
            Currency.USD,
            BigDecimal.valueOf(25));

    @BeforeEach
    void setUp() {
        quoteService = new QuoteService(quoteRepository, quoteArticleRepository);
    }

    @Test
    void givenQuotesExist_whenGetAllQuotes_thenReturnAllQuotes() {
        // GIVEN
        when(quoteRepository.findAll(PAGEABLE)).thenReturn(QUOTES_PAGE);

        // WHEN
        Page<QuoteDTO> retrievedQuotes = quoteService.getAllQuotes(PAGEABLE);

        // THEN
        assertEquals(QUOTES, retrievedQuotes.getContent());
    }

    @Test
    void givenFilter_whenGetQuotesByFilter_thenReturnFilteredQuotes() {
        // GIVEN
        QuoteFilter filter = new QuoteFilter(of(USER.slug()), empty(), empty(), empty());
        List<QuoteDTO> filteredQuotes = List.of(QUOTE_1, QUOTE_3);
        Page<QuoteDTO> pageFilteredQuotes = new PageImpl<>(filteredQuotes, PAGEABLE, filteredQuotes.size());
        when(quoteRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageFilteredQuotes);

        // WHEN
        Page<QuoteDTO> retrievedQuotes = quoteService.getQuotesByFilter(filter, PAGEABLE);

        // THEN
        assertEquals(2, retrievedQuotes.getContent().size());
        assertEquals(QUOTE_1, retrievedQuotes.getContent().get(0));
        assertEquals(QUOTE_3, retrievedQuotes.getContent().get(1));
    }

    @Test
    void givenQuoteExists_whenGetQuote_thenReturnQuote() {
        // GIVEN
        when(quoteRepository.findBySlug(QUOTE_1.slug())).thenReturn(QUOTE_1);

        // WHEN
        QuoteDTO retrievedQuote = quoteService.getQuote(QUOTE_1.slug());

        // THEN
        assertEquals(QUOTE_1, retrievedQuote);
    }

    @Test
    void givenUnknownSlug_whenGetQuote_thenThrowUnknownQuoteException() {
        // GIVEN
        when(quoteRepository.findBySlug(anyString())).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> quoteService.getQuote("unknown-slug");

        // THEN
        assertThrows(UnknownQuoteException.class, executable);
    }

    @Test
    void givenValidUpdateQuoteCommand_whenUpdateQuote_thenReturnUpdatedQuote() {
        // GIVEN
        when(quoteRepository.update(UPDATE_QUOTE_COMMAND, SLUG_1)).thenReturn(QUOTE_1);

        // WHEN
        QuoteDTO updatedQuote = quoteService.updateQuote(UPDATE_QUOTE_COMMAND, SLUG_1);

        // THEN
        assertEquals(QUOTE_1, updatedQuote);
        verify(quoteRepository).update(UPDATE_QUOTE_COMMAND, SLUG_1);
    }

    @Test
    void givenUnknownSlug_whenUpdateQuote_thenThrowUnknownQuoteException() {
        // GIVEN
        when(quoteRepository.update(UPDATE_QUOTE_COMMAND, SLUG_1))
                .thenThrow(new UnknownSlugException("Slug not found"));

        // WHEN & THEN
        assertThrows(UnknownQuoteException.class, () -> quoteService.updateQuote(UPDATE_QUOTE_COMMAND, SLUG_1));
    }

    @Test
    void givenQuoteSlug_whenDeleteQuote_thenSuccess() {
        // GIVEN
        doNothing().when(quoteRepository).deleteQuoteBySlug(QUOTE_1.slug());

        // WHEN
        Executable executable = () -> quoteService.deleteQuote(QUOTE_1.slug());

        // THEN
        assertDoesNotThrow(executable);
        verify(quoteRepository).deleteQuoteBySlug(QUOTE_1.slug());
    }

    @Test
    void givenUnknownSlug_whenDeleteQuote_thenThrowUnknownQuoteException() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(quoteRepository).deleteQuoteBySlug(anyString());

        // WHEN
        Executable executable = () -> quoteService.deleteQuote("unknown-slug");

        // THEN
        assertThrows(UnknownQuoteException.class, executable);
    }

    @Test
    void givenValidCreateQuoteCommand_whenCreateQuote_thenSuccess() {
        // GIVEN
        when(quoteRepository.isQuoteNameUsedByUser(CREATE_COMMAND.name(), USER.slug())).thenReturn(false);

        // WHEN
        Executable executable = () -> quoteService.createQuote(CREATE_COMMAND, USER);

        // THEN
        assertDoesNotThrow(executable);
        verify(quoteRepository).save(CREATE_COMMAND, USER);
    }

    @Test
    void givenQuoteNameAlreadyUsed_whenCreateQuote_thenThrowQuoteUnityException() {
        // GIVEN
        when(quoteRepository.isQuoteNameUsedByUser(CREATE_COMMAND.name(), USER.slug())).thenReturn(true);

        // WHEN
        Executable executable = () -> quoteService.createQuote(CREATE_COMMAND, USER);

        // THEN
        assertThrows(QuoteUnityException.class, executable);
    }

    @Test
    void givenQuoteArticleExists_whenGetQuoteArticle_thenReturnQuoteArticle() {
        // GIVEN
        QuoteArticleDTO article = QuoteArticleFactory.buildDefaultQuoteArticle();
        when(quoteArticleRepository.findBySlug("articleSlug")).thenReturn(article);

        // WHEN
        QuoteArticleDTO retrieved = quoteService.getQuoteArticle("articleSlug");

        // THEN
        assertEquals(article, retrieved);
    }

    @Test
    void givenUnknownSlug_whenGetQuoteArticle_thenThrowUnknownQuoteArticleException() {
        // GIVEN
        when(quoteArticleRepository.findBySlug(anyString())).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> quoteService.getQuoteArticle("unknown");

        // THEN
        assertThrows(UnknownQuoteArticleException.class, executable);
    }

    @Test
    void givenValidCreateQuoteArticleCommand_whenAddQuoteArticle_thenSuccess() {
        // GIVEN
        QuoteArticleDTO article = QuoteArticleFactory.buildDefaultQuoteArticle();
        when(quoteArticleRepository.save(CREATE_QUOTE_ARTICLE_COMMAND)).thenReturn(article);

        // WHEN
        QuoteArticleDTO result = quoteService.addQuoteArticle(CREATE_QUOTE_ARTICLE_COMMAND);

        // THEN
        assertEquals(article, result);
        verify(quoteArticleRepository).save(CREATE_QUOTE_ARTICLE_COMMAND);
    }

    @Test
    void givenValidUpdateQuoteArticleCommand_whenUpdateQuoteArticle_thenReturnUpdatedArticle() {
        // GIVEN
        QuoteArticleDTO article = QuoteArticleFactory.buildDefaultQuoteArticle();
        when(quoteArticleRepository.update(UPDATE_QUOTE_ARTICLE_COMMAND, "articleSlug")).thenReturn(article);

        // WHEN
        QuoteArticleDTO result = quoteService.updateQuoteArticle(UPDATE_QUOTE_ARTICLE_COMMAND, "articleSlug");

        // THEN
        assertEquals(article, result);
    }

    @Test
    void givenUnknownSlug_whenUpdateQuoteArticle_thenThrowUnknownQuoteArticleException() {
        // GIVEN
        when(quoteArticleRepository.update(UPDATE_QUOTE_ARTICLE_COMMAND, "articleSlug1"))
                .thenThrow(UnknownSlugException.class);

        // WHEN & THEN
        assertThrows(UnknownQuoteArticleException.class,
                () -> quoteService.updateQuoteArticle(UPDATE_QUOTE_ARTICLE_COMMAND, "articleSlug1"));
    }

    @Test
    void givenQuoteArticleSlug_whenDeleteQuoteArticle_thenSuccess() {
        // GIVEN
        doNothing().when(quoteArticleRepository).deleteArticleQuoteBySlug("articleSlug1");

        // WHEN
        Executable executable = () -> quoteService.deleteQuoteArticle("articleSlug1");

        // THEN
        assertDoesNotThrow(executable);
        verify(quoteArticleRepository).deleteArticleQuoteBySlug("articleSlug1");
    }

    @Test
    void givenUnknownSlug_whenDeleteQuoteArticle_thenThrowUnknownQuoteArticleException() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(quoteArticleRepository).deleteArticleQuoteBySlug(anyString());

        // WHEN
        Executable executable = () -> quoteService.deleteQuoteArticle("unknown-slug");

        // THEN
        assertThrows(UnknownQuoteArticleException.class, executable);
    }

}
