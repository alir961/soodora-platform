package com.soodora.api.quote;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quote.QuoteFilter;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.user.UserDTO;
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
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
@Tag(name = "Quote Controller")
public class QuoteController implements QuoteResource {

    private final QuoteService quoteService;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping
    public ResponseEntity<JsonResponse> createQuote(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreateQuoteRequest request) {
        try {
            QuoteDTO quote = quoteService.createQuote(request.toCommand(), user);
            return new ResponseEntity<>(new QuoteResponse(quote), CREATED);
        } catch (InvalidRequestException | QuoteUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateQuote(
            @PathVariable String slug,
            @RequestBody UpdateQuoteRequest request) {
        try {
            QuoteDTO quote = quoteService.updateQuote(request.toCommand(), slug);
            return new ResponseEntity<>(new QuoteResponse(quote), OK);
        } catch (InvalidRequestException | UnknownQuoteException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}")
    public ResponseEntity<JsonResponse> getQuote(@PathVariable String slug) {
        try {
            QuoteDTO quote = quoteService.getQuote(slug);
            return new ResponseEntity<>(new QuoteResponse(quote), OK);
        } catch (UnknownQuoteException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        }
    }

    @GetMapping("/search")
    public Page<QuoteDTO> getQuotes(@RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "category") Optional<String> category,
            Pageable pageable) {
        QuoteFilter filter = new QuoteFilter(userSlug, name, description, category);
        return quoteService.getQuotesByFilter(filter, pageable);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/{slug}")
    public void deleteQuote(@PathVariable String slug) {
        quoteService.deleteQuote(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping("/articles")
    public ResponseEntity<JsonResponse> createQuoteArticle(@RequestBody CreateQuoteArticleRequest request) {
        try {
            QuoteArticleDTO article = quoteService.addQuoteArticle(request.toCommand());
            return new ResponseEntity<>(new QuoteArticleResponse(article), CREATED);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @PutMapping("/articles/{slug}")
    public ResponseEntity<JsonResponse> updateQuoteArticle(
            @PathVariable String slug,
            @RequestBody UpdateQuoteArticleRequest request) {
        try {
            QuoteArticleDTO article = quoteService.updateQuoteArticle(request.toCommand(), slug);
            return new ResponseEntity<>(new QuoteArticleResponse(article), OK);
        } catch (InvalidRequestException | UnknownQuoteArticleException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/articles/{slug}")
    public ResponseEntity<JsonResponse> getQuoteArticle(@PathVariable String slug) {
        try {
            QuoteArticleDTO article = quoteService.getQuoteArticle(slug);
            return new ResponseEntity<>(new QuoteArticleResponse(article), OK);
        } catch (UnknownQuoteArticleException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), NOT_FOUND);
        }
    }

    @GetMapping("/articles")
    public Page<QuoteArticleDTO> getAllQuoteArticles(Pageable pageable) {
        return quoteService.getAllQuoteArticles(pageable);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @DeleteMapping("/articles/{slug}")
    public void deleteQuoteArticle(@PathVariable String slug) {
        quoteService.deleteQuoteArticle(slug);
    }

}
