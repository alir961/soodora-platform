package com.soodora.api.product;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductAnalytics;
import com.soodora.domain.product.ProductFilter;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.product.CreateProductRequest;
import com.soodora.service.product.ProductService;
import com.soodora.service.product.ProductUnityException;
import com.soodora.service.product.UpdateProductRequest;
import com.soodora.service.shared.FileDataService;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller")
public class ProductController implements ProductResource {

    private final ProductService service;
    private final FileDataService fileDataService;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> createProduct(
            @AuthenticationPrincipal User user,
            @RequestPart("data") CreateProductRequest createProductRequest,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);

            service.createProduct(createProductRequest.toCommand(), user, fileData);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | ProductUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @GetMapping("/search")
    public Page<ProductDTO> searchProducts(@RequestParam(value = "userSlug") Optional<String> userSlug,
            @RequestParam(value = "name") Optional<String> name,
            @RequestParam(value = "description") Optional<String> description,
            @RequestParam(value = "category") Optional<String> category,
            Pageable pageable) {
        ProductFilter filter = new ProductFilter(userSlug, name, description, category);

        return service.retrieveProducts(filter, pageable)
                .map(ProductDTO::build);
    }

    @Override
    @GetMapping("/{slug}")
    public ProductDTO getProduct(@PathVariable String slug) {
        return ProductDTO.build(service.retrieveProduct(slug));
    }

    @Override
    @PutMapping("/{slug}")
    @PreAuthorize("hasRole('ARTIST')")
    public ResponseEntity<JsonResponse> updateProduct(
            @AuthenticationPrincipal User user,
            @PathVariable String slug,
            @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            Product product = service.updateProduct(user, slug, request.toCommand(), fileData);

            return new ResponseEntity<>(ProductDTO.build(product), OK);
        } catch (InvalidRequestException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{slug}")
    @PreAuthorize("hasRole('ARTIST')")
    public void deleteProduct(@AuthenticationPrincipal User user, @PathVariable("slug") String slug) {
        service.deleteProduct(user, slug);
    }

    @Override
    @GetMapping("/analytics")
    @PreAuthorize("hasRole('ARTIST')")
    public ProductAnalyticsDTO getGlobalAnalytics(@AuthenticationPrincipal User user) {
        ProductAnalytics analytics = service.computeGlobalAnalytics(user);

        return ProductAnalyticsDTO.build(analytics);
    }

    @Override
    @GetMapping("/{slug}/analytics")
    @PreAuthorize("hasRole('ARTIST')")
    public ProductAnalyticsDTO getProductAnalytics(@AuthenticationPrincipal User user, @PathVariable String slug) {
        ProductAnalytics analytics = service.computeProductAnalytics(user, slug);

        return ProductAnalyticsDTO.build(analytics);
    }

}
