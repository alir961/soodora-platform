package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.exception.InvalidRequestException;
import com.grooveconductor.exception.ProductUnityException;
import com.grooveconductor.request.CreateProductRequest;
import com.grooveconductor.rest.response.CreateProductResponse;
import com.grooveconductor.rest.response.ExceptionResponse;
import com.grooveconductor.rest.response.JsonResponse;
import com.grooveconductor.service.ProductService;
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

    @GetMapping
    public List<ProductDTO> getProducts() {
        return service.retrieveAllProducts();
    }

    @GetMapping("/{slug}")
    public ProductDTO getProduct(@PathVariable String slug) {
        return service.retrieveProduct(slug);
    }

    @DeleteMapping("/{slug}")
    public void deleteProduct(@PathVariable("slug") String slug) {
        service.deleteBySlug(slug);
    }

    @PostMapping
    public ResponseEntity<JsonResponse> createProduct(@RequestBody CreateProductRequest createProductRequest) {
        try {
            String slug = service.createProduct(createProductRequest.toCommand());

            return new ResponseEntity<>(new CreateProductResponse(slug), CREATED);
        } catch (InvalidRequestException | ProductUnityException exception) {
            return new ResponseEntity<>(new ExceptionResponse(exception.getMessage()), BAD_REQUEST);
        }
    }

}
