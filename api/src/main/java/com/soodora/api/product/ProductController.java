package com.soodora.api.product;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductFilter;
import com.soodora.domain.product.UpdateProductCommand;
import com.soodora.domain.user.UserDTO;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.product.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller")
public class ProductController {

    private final ProductService service;

    @SecurityRequirement(name = "bearerAuth")
    @PreAuthorize("hasRole('ARTIST')")
    @PostMapping
    public ResponseEntity<JsonResponse> createProduct(
            @AuthenticationPrincipal UserDTO user,
            @RequestPart("data") CreateProductRequest createProductRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            service.createProduct(createProductRequest.toCommand(), user, imageFile);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | ProductUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public Page<ProductDTO> getProducts(@RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "category") Optional<String> category,
            Pageable pageable) {

        ProductFilter filter = new ProductFilter(userSlug, name, description, category);

        return service.retrieveProducts(filter, pageable);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}")
    public ProductDTO getProduct(@PathVariable String slug) {
        return service.retrieveProduct(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateProduct(
            @PathVariable String slug,
            @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            UpdateProductCommand command = request.toCommand();

            service.updateProduct(command, slug, imageFile);
            return new ResponseEntity<>(ACCEPTED);
        } catch (InvalidRequestException | UnknownProductException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        service.deleteProductBySlug(slug);
    }

}
