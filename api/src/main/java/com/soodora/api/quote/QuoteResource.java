package com.soodora.api.quote;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.quote.CreateQuoteArticleRequest;
import com.soodora.service.quote.CreateQuoteRequest;
import com.soodora.service.quote.UpdateQuoteArticleRequest;
import com.soodora.service.quote.UpdateQuoteRequest;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RequestMapping("/quotes")
@SecurityRequirement(name = "bearerAuth")
public interface QuoteResource {

    @PostMapping
    ResponseEntity<JsonResponse> createQuote(@AuthenticationPrincipal UserDTO user,
            @RequestBody CreateQuoteRequest request);

    @PutMapping("/{slug}")
    ResponseEntity<JsonResponse> updateQuote(@PathVariable String slug, @RequestBody UpdateQuoteRequest request);

    @GetMapping("/{slug}")
    ResponseEntity<JsonResponse> getQuote(@PathVariable String slug);

    @GetMapping
    Page<QuoteDTO> getQuotes(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "category") Optional<String> category,
            Pageable pageable);

    @DeleteMapping("/{slug}")
    void deleteQuote(@PathVariable String slug);

    @PostMapping("/articles")
    ResponseEntity<JsonResponse> createQuoteArticle(@RequestBody CreateQuoteArticleRequest request);

    @PutMapping("/articles/{slug}")
    ResponseEntity<JsonResponse> updateQuoteArticle(@PathVariable String slug,
            @RequestBody UpdateQuoteArticleRequest request);

    @GetMapping("/articles/{slug}")
    ResponseEntity<JsonResponse> getQuoteArticle(@PathVariable String slug);

    @GetMapping("/articles")
    Page<QuoteArticleDTO> getAllQuoteArticles(Pageable pageable);

    @DeleteMapping("/articles/{slug}")
    void deleteQuoteArticle(@PathVariable String slug);

}
