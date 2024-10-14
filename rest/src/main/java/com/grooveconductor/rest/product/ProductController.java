package com.grooveconductor.rest.product;

import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.product.ProductFilter;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.product.ProductUnityException;
import com.grooveconductor.application.product.CreateProductRequest;
import com.grooveconductor.rest.common.ExceptionResponse;
import com.grooveconductor.rest.common.JsonResponse;
import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.product.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Product Controller")
public class ProductController {

    private final ProductService service;
    private final AuthenticationService authenticationService;

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<JsonResponse> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        try {
            UserDTO user = authenticationService.getAuthenticatedUser();

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
    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        service.deleteProductBySlug(slug);
    }

}
