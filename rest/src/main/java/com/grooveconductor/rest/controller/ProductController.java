package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.exception.InvalidRequestException;
import com.grooveconductor.exception.ProductUnityException;
import com.grooveconductor.request.CreateProductRequest;
import com.grooveconductor.rest.response.ExceptionResponse;
import com.grooveconductor.rest.response.JsonResponse;
import com.grooveconductor.service.AuthenticationService;
import com.grooveconductor.service.ProductService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;


@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService service;
    private final AuthenticationService authenticationService;

    @GetMapping
    public List<ProductDTO> getProducts() {
        return service.retrieveAllProducts();
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{slug}")
    public ProductDTO getProduct(@PathVariable String slug) {
        return service.retrieveProduct(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        service.deleteBySlug(slug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user")
    public List<ProductDTO> getProductsOfUser(@RequestParam("userSlug") String userSlug) {
        return service.retrieveAllProductsOfUserBySlug(userSlug);
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<JsonResponse> createPlaylist(@RequestBody CreateProductRequest createPlaylistRequest) {
        UserDTO user = authenticationService.getAuthenticatedUser();
        try {
            service.createProduct(createPlaylistRequest.toCommand(), user);

            return new ResponseEntity<>(CREATED);
        } catch (InvalidRequestException | ProductUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

}
