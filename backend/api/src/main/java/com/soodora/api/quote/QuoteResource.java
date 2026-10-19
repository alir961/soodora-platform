package com.soodora.api.quote;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.quote.QuoteDTO;
import com.soodora.domain.quotearticle.QuoteArticleDTO;
import com.soodora.domain.user.User;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Quote Controller")
public interface QuoteResource {

    @Operation(summary = "Create a new quote", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping
    ResponseEntity<JsonResponse> createQuote(@AuthenticationPrincipal User user,
            @RequestBody CreateQuoteRequest request);

    @Operation(summary = "Update an existing quote", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote updated successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    @PutMapping("/{slug}")
    ResponseEntity<JsonResponse> updateQuote(@AuthenticationPrincipal User user, @PathVariable String slug,
            @RequestBody UpdateQuoteRequest request);

    @Operation(summary = "Get a quote by slug", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    @GetMapping("/{slug}")
    ResponseEntity<JsonResponse> getQuote(@AuthenticationPrincipal User user, @PathVariable String slug);

    @Operation(summary = "List quotes with optional filters", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quotes retrieved successfully")
    })
    @GetMapping
    Page<QuoteDTO> getQuotes(@AuthenticationPrincipal User user,
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "category") Optional<String> category,
            Pageable pageable);

    @Operation(summary = "Delete a quote by slug", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "204", description = "Quote deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    @DeleteMapping("/{slug}")
    void deleteQuote(@AuthenticationPrincipal User user, @PathVariable String slug);

    @Operation(summary = "Create a new quote article", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote article created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/articles")
    ResponseEntity<JsonResponse> createQuoteArticle(@AuthenticationPrincipal User user,
            @RequestBody CreateQuoteArticleRequest request);

    @Operation(summary = "Update an existing quote article", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote article updated successfully"),
            @ApiResponse(responseCode = "404", description = "Quote article not found")
    })
    @PutMapping("/articles/{slug}")
    ResponseEntity<JsonResponse> updateQuoteArticle(@AuthenticationPrincipal User user, @PathVariable String slug,
            @RequestBody UpdateQuoteArticleRequest request);

    @Operation(summary = "Get a quote article by slug", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote article retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Quote article not found")
    })
    @GetMapping("/articles/{slug}")
    ResponseEntity<JsonResponse> getQuoteArticle(@AuthenticationPrincipal User user, @PathVariable String slug);

    @Operation(summary = "List all quote articles", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Quote articles retrieved successfully")
    })
    @GetMapping("/articles")
    Page<QuoteArticleDTO> getAllQuoteArticles(@AuthenticationPrincipal User user, Pageable pageable);

    @Operation(summary = "Delete a quote article by slug", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "204", description = "Quote article deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Quote article not found")
    })
    @DeleteMapping("/articles/{slug}")
    void deleteQuoteArticle(@AuthenticationPrincipal User user, @PathVariable String slug);

    @Operation(summary = "Export a quote as PDF", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "PDF exported successfully"),
            @ApiResponse(responseCode = "404", description = "Quote not found")
    })
    @GetMapping("/{slug}/export/pdf")
    ResponseEntity<?> exportPdf(@AuthenticationPrincipal User user, @PathVariable String slug);

}
