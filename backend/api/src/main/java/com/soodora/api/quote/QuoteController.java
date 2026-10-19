package com.soodora.api.quote;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quote.QuoteFilter;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.quote.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_PDF;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ARTIST')")
public class QuoteController implements QuoteResource {

    private final QuoteService quoteService;

    @PostMapping
    public ResponseEntity<JsonResponse> createQuote(
            @AuthenticationPrincipal User user,
            @RequestBody CreateQuoteRequest request) {
        try {
            QuoteDTO quote = quoteService.createQuote(request.toCommand(), user);

            return new ResponseEntity<>(new QuoteResponse(quote), CREATED);
        } catch (InvalidRequestException | QuoteUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateQuote(@AuthenticationPrincipal User user,
            @PathVariable String slug,
            @RequestBody UpdateQuoteRequest request) {
        try {
            QuoteDTO quote = quoteService.updateQuote(request.toCommand(), slug, user);

            return new ResponseEntity<>(new QuoteResponse(quote), OK);
        } catch (InvalidRequestException | UnknownQuoteException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<JsonResponse> getQuote(@AuthenticationPrincipal User user, @PathVariable String slug) {
        try {
            QuoteDTO quote = quoteService.getQuote(slug, user);

            return new ResponseEntity<>(new QuoteResponse(quote), OK);
        } catch (UnknownQuoteException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public Page<QuoteDTO> getQuotes(@AuthenticationPrincipal User user,
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "clientName") Optional<String> clientName,
            Pageable pageable) {
        QuoteFilter filter = new QuoteFilter(userSlug, name, description, clientName);

        return quoteService.getQuotesByFilter(filter, user, pageable);
    }

    @DeleteMapping("/{slug}")
    public void deleteQuote(@AuthenticationPrincipal User user, @PathVariable String slug) {
        quoteService.deleteQuote(slug, user);
    }

    @PostMapping("/articles")
    public ResponseEntity<JsonResponse> createQuoteArticle(@AuthenticationPrincipal User user,
            @RequestBody CreateQuoteArticleRequest request) {
        try {
            QuoteArticleDTO article = quoteService.addQuoteArticle(request.toCommand(), user);

            return new ResponseEntity<>(new QuoteArticleResponse(article), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @PutMapping("/articles/{slug}")
    public ResponseEntity<JsonResponse> updateQuoteArticle(@AuthenticationPrincipal User user,
            @PathVariable String slug,
            @RequestBody UpdateQuoteArticleRequest request) {
        try {
            QuoteArticleDTO article = quoteService.updateQuoteArticle(request.toCommand(), slug, user);

            return new ResponseEntity<>(new QuoteArticleResponse(article), OK);
        } catch (InvalidRequestException | UnknownQuoteArticleException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/articles/{slug}")
    public ResponseEntity<JsonResponse> getQuoteArticle(@AuthenticationPrincipal User user, @PathVariable String slug) {
        try {
            QuoteArticleDTO article = quoteService.getQuoteArticle(slug, user);

            return new ResponseEntity<>(new QuoteArticleResponse(article), OK);
        } catch (UnknownQuoteArticleException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        }
    }

    @GetMapping("/articles")
    public Page<QuoteArticleDTO> getAllQuoteArticles(@AuthenticationPrincipal User user, Pageable pageable) {
        return quoteService.getAllQuoteArticles(user, pageable);
    }

    @DeleteMapping("/articles/{slug}")
    public void deleteQuoteArticle(@AuthenticationPrincipal User user, @PathVariable String slug) {
        quoteService.deleteQuoteArticle(slug, user);
    }

    @GetMapping("/{slug}/export/pdf")
    public ResponseEntity<?> exportPdf(@AuthenticationPrincipal User user, @PathVariable String slug) {
        // Outside the try: permission and not-found errors keep their own status codes
        quoteService.getQuote(slug, user);
        try {
            byte[] pdfBytes = quoteService.exportQuoteToPdf(slug, user);

            return ok()
                    .header(CONTENT_DISPOSITION, "attachment; filename=\"quote_" + slug + ".pdf\"")
                    .contentType(APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return new ResponseEntity<>(new ExceptionResponse("Failed to export PDF: " + e.getMessage()),
                    INTERNAL_SERVER_ERROR);
        }
    }

}
