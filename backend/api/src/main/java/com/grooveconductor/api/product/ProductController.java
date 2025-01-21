package com.grooveconductor.api.product;

import com.grooveconductor.api.common.ExceptionResponse;
import com.grooveconductor.api.common.JsonResponse;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.product.ProductFilter;
import com.grooveconductor.domain.product.UpdateProductCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.product.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller")
public class ProductController {

    private final ProductService service;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<JsonResponse> createProduct(
            @AuthenticationPrincipal UserDTO user,
            @RequestBody CreateProductRequest createProductRequest
    ) {
        try {
            service.createProduct(createProductRequest.toCommand(), user);
            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | ProductUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

    @GetMapping
    public List<ProductDTO> getProducts(@RequestParam(value = "userSlug", required = false) String userSlug) {
        ProductFilter filter = new ProductFilter(ofNullable(userSlug));

        return service.retrieveProducts(filter);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}")
    public ProductDTO getProduct(@PathVariable String slug) {
        return service.retrieveProduct(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{slug}")
    public ResponseEntity<JsonResponse> updateProduct(@PathVariable String slug, @RequestBody UpdateProductRequest request) {
        try {
            UpdateProductCommand command = request.toCommand();

            service.updateProduct(command, slug);
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
