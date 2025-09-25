package com.soodora.api.quote;

import com.soodora.domain.common.Currency;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quote.QuoteFilter;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.quote.*;
import com.soodora.test.factory.QuoteArticleFactory;
import com.soodora.test.factory.QuoteFactory;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.ARTIST_USER;
import static com.soodora.test.TestConstants.USER;

@SpringBootTest
@AutoConfigureMockMvc
class QuoteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private QuoteService quoteService;

    @MockitoBean
    private UserRepository userRepository;

    private static final QuoteDTO A_QUOTE = QuoteFactory.buildDefaultQuote("1");
    private static final QuoteArticleDTO A_QUOTE_ARTICLE = QuoteArticleFactory.buildDefaultQuoteArticle();

    @Test
    void givenArtistUser_whenCreateQuote_thenSuccess() throws Exception {
        // GIVEN
        CreateQuoteRequest request = new CreateQuoteRequest("Sample Quote", "Client", "email@example.com", "Address",
                "123456", "Desc");
        when(quoteService.createQuote(any(), eq(ARTIST_USER))).thenReturn(A_QUOTE);
        when(userRepository.find(ARTIST_USER.identifier())).thenReturn(ARTIST_USER);

        // WHEN & THEN
        mockMvc.perform(post("/quotes")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteDTO.name").value("Sample Quote"));
    }

    @Test
    void givenInvalidRequest_whenCreateQuote_thenBadRequest() throws Exception {
        // GIVEN
        CreateQuoteRequest request = new CreateQuoteRequest(null, null, null, null, null, null);
        doThrow(InvalidRequestException.class).when(quoteService).createQuote(any(), eq(ARTIST_USER));
        when(userRepository.find(ARTIST_USER.identifier())).thenReturn(ARTIST_USER);

        // WHEN & THEN
        mockMvc.perform(post("/quotes")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoArtistUser_whenCreateQuote_thenForbidden() throws Exception {
        // GIVEN
        CreateQuoteRequest request = new CreateQuoteRequest("Quote Name", "Client", "email@example.com", "Address",
                "123456", "Desc");

        // WHEN & THEN
        mockMvc.perform(post("/quotes")
                .with(user(new UserPrincipal(USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetQuote_thenSuccess() throws Exception {
        // GIVEN
        when(quoteService.getQuote(A_QUOTE.slug())).thenReturn(A_QUOTE);

        // WHEN & THEN
        mockMvc.perform(get("/quotes/" + A_QUOTE.slug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteDTO.slug").value(A_QUOTE.slug()));
    }

    @Test
    void givenInvalidSlug_whenGetQuote_thenNotFound() throws Exception {
        // GIVEN
        doThrow(new UnknownQuoteException("Not found")).when(quoteService).getQuote("invalidSlug");

        // WHEN & THEN
        mockMvc.perform(get("/quotes/invalidSlug")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidPage_whenGetQuotes_thenSuccess() throws Exception {
        // GIVEN
        Page<QuoteDTO> page = new PageImpl<>(List.of(A_QUOTE));
        when(quoteService.getQuotesByFilter(any(QuoteFilter.class), any(Pageable.class))).thenReturn(page);

        // WHEN & THEN
        mockMvc.perform(get("/quotes/search")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void givenValidRequest_whenUpdateQuote_thenSuccess() throws Exception {
        // GIVEN
        UpdateQuoteRequest request = new UpdateQuoteRequest("Updated Name", "Client", "email@example.com", "Addr", "123",
                "Desc");
        when(quoteService.updateQuote(any(), eq(A_QUOTE.slug()))).thenReturn(A_QUOTE);

        // WHEN & THEN
        mockMvc.perform(put("/quotes/" + A_QUOTE.slug())
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidSlug_whenDeleteQuote_thenSuccess() throws Exception {
        // GIVEN
        doNothing().when(quoteService).deleteQuote(A_QUOTE.slug());

        // WHEN & THEN
        mockMvc.perform(delete("/quotes/" + A_QUOTE.slug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());

        // THEN
        verify(quoteService).deleteQuote(A_QUOTE.slug());
    }

    @Test
    void givenValidRequest_whenCreateQuoteArticle_thenSuccess() throws Exception {
        // GIVEN
        CreateQuoteArticleRequest request = new CreateQuoteArticleRequest("quoteSlug", "Desc", 2,
                BigDecimal.valueOf(50.0), Currency.EUR,
                BigDecimal.valueOf(20));
        when(quoteService.addQuoteArticle(any())).thenReturn(A_QUOTE_ARTICLE);

        // WHEN & THEN
        mockMvc.perform(post("/quotes/articles")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quoteArticleDTO.slug").value("articleSlug"));
    }

    @Test
    void givenInvalidRequest_whenCreateQuoteArticle_thenBadRequest() throws Exception {
        // GIVEN
        CreateQuoteArticleRequest request = new CreateQuoteArticleRequest("quoteSlug", null, 0, BigDecimal.valueOf(0.0),
                null, null);
        doThrow(InvalidRequestException.class).when(quoteService).addQuoteArticle(any());

        // WHEN & THEN
        mockMvc.perform(post("/quotes/articles")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidSlug_whenGetQuoteArticle_thenSuccess() throws Exception {
        // GIVEN
        when(quoteService.getQuoteArticle(A_QUOTE_ARTICLE.slug())).thenReturn(A_QUOTE_ARTICLE);

        // WHEN & THEN
        mockMvc.perform(get("/quotes/articles/" + A_QUOTE_ARTICLE.slug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteArticleDTO.slug").value(A_QUOTE_ARTICLE.slug()));
    }

    @Test
    void givenInvalidSlug_whenGetQuoteArticle_thenNotFound() throws Exception {
        // GIVEN
        doThrow(UnknownQuoteArticleException.class).when(quoteService).getQuoteArticle("invalid");

        // WHEN & THEN
        mockMvc.perform(get("/quotes/articles/invalid")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidSlug_whenDeleteQuoteArticle_thenSuccess() throws Exception {
        // GIVEN
        doNothing().when(quoteService).deleteQuoteArticle(A_QUOTE_ARTICLE.slug());

        // WHEN & THEN
        mockMvc.perform(delete("/quotes/articles/" + A_QUOTE_ARTICLE.slug())
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());

        // THEN
        verify(quoteService).deleteQuoteArticle(A_QUOTE_ARTICLE.slug());
    }

    @Test
    void givenValidQuoteSlug_whenExportQuoteToPdf_thenReturnsPdf() throws Exception {
        // GIVEN
        byte[] pdfData = "PDFDATA".getBytes();
        when(quoteService.exportQuoteToPdf("quoteSlug1"))
                .thenReturn(pdfData);

        // WHEN & THEN
        mockMvc.perform(get("/quotes/quoteSlug1/export/pdf")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_PDF))
                .andExpect(header().string(CONTENT_DISPOSITION, containsString("quote_quoteSlug1.pdf")));
    }

    @Test
    void givenNoAuthentication_whenExportQuoteToPdf_thenReturnsForbidden() throws Exception {
        // GIVEN // WHEN // THEN
        mockMvc.perform(get("/quotes/quoteSlug1/export/pdf"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenQuoteExportFails_whenExportQuoteToPdf_thenReturnsInternalServerError() throws Exception {
        // GIVEN
        when(quoteService.exportQuoteToPdf("quoteSlug1"))
                .thenThrow(new RuntimeException("PDF generation failed"));

        // WHEN // THEN
        mockMvc.perform(get("/quotes/quoteSlug1/export/pdf")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

}
