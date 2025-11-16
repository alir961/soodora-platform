package com.soodora.api.product;

import com.soodora.api.common.JsonResponse;
import com.soodora.domain.user.User;
import com.soodora.service.product.CreateProductRequest;
import com.soodora.service.product.UpdateProductRequest;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product Controller")
public interface ProductResource {

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new product", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    ResponseEntity<JsonResponse> createProduct(
            @AuthenticationPrincipal User user,
            @RequestPart("data") CreateProductRequest createProductRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @Operation(summary = "Search products with filters", responses = {
            @ApiResponse(responseCode = "200", description = "List of products retrieved successfully")
    })
    Page<ProductDTO> getProducts(
            @RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "category") Optional<String> category,
            Pageable pageable);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get product by slug", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    ProductDTO getProduct(@PathVariable String slug);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update a product", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    ResponseEntity<JsonResponse> updateProduct(
            @PathVariable String slug,
            @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile);

    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete a product", security = @SecurityRequirement(name = "bearerAuth"), responses = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @io.swagger.v3.oas.annotations.media.Content)
    })
    void deleteProduct(@PathVariable("slug") String slug);

}
