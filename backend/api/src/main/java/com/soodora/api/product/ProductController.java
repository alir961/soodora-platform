package com.soodora.api.product;

import com.soodora.api.common.ExceptionResponse;
import com.soodora.api.common.JsonResponse;
import com.soodora.domain.cdn.FileData;
import com.soodora.domain.product.Product;
import com.soodora.domain.product.ProductFilter;
import com.soodora.domain.user.User;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.product.*;
import com.soodora.service.shared.FileDataService;
import com.soodora.service.shared.InvalidPermissionException;

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
    public Page<ProductDTO> getProducts(@RequestParam(value = "userSlug") Optional<String> userSlug,
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
    public ResponseEntity<JsonResponse> updateProduct(
            @PathVariable String slug,
            @RequestPart("data") UpdateProductRequest request,
            @RequestPart(value = "file", required = false) MultipartFile imageFile) {
        try {
            FileData fileData = fileDataService.toFileData(imageFile);
            Product product = service.updateProduct(request.toCommand(), slug, fileData);

            return new ResponseEntity<>(ProductDTO.build(product), OK);
        } catch (InvalidRequestException | UnknownProductException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @Override
    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        service.deleteProductBySlug(slug);
    }

    @Override
    @GetMapping("/{slug}/analytics")
    public ProductAnalyticsDTO getProductAnalytics(@AuthenticationPrincipal User user, @PathVariable String slug) {
        Product product = service.retrieveProduct(slug);

        if (product.getVendor().getSlug().equals(user.getSlug()) || user.isAdmin())
            return ProductAnalyticsDTO.build(service.computeProductAnalytics(slug));
        throw new InvalidPermissionException("Only the product vendor can access the analytics");
    }

}
