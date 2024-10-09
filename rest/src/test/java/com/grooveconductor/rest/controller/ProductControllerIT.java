package com.grooveconductor.rest.controller;

import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.exception.InvalidRequestException;
import com.grooveconductor.request.CreateProductRequest;
import com.grooveconductor.service.AuthenticationService;
import com.grooveconductor.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.grooveconductor.rest.JsonStringify.asJsonString;
import static com.grooveconductor.test.ProductFactory.buildDefaultProduct;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private AuthenticationService authenticationService;

    @Test
    void getProductsSuccess() throws Exception {
        // GIVEN
        ProductDTO product = buildDefaultProduct();
        when(productService.retrieveAllProducts()).thenReturn(Collections.singletonList(product));

        // WHEN & THEN
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].slug").value("product-slug"))
            .andExpect(jsonPath("$[0].name").value("Product Name"));
    }

    @Test
    void getProductSuccess() throws Exception {
        // GIVEN
        ProductDTO product = buildDefaultProduct();
        when(productService.retrieveProduct("product-slug")).thenReturn(product);

        // WHEN & THEN
        mockMvc.perform(get("/products/product-slug"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.slug").value("product-slug"))
            .andExpect(jsonPath("$.name").value("Product Name"));
    }

    @Test
    void deleteProductSuccess() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/products/product-slug"))
            .andExpect(status().isOk());
    }

    @Test
    void createProductSuccess() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest("Product Name", "Description", "image-url", "category", 100, 10);
        UserDTO user = buildDefaultUser();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        // For void methods, use doNothing().when(...)
        doNothing().when(productService).createProduct(any(), any());

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isCreated());
    }


    @Test
    void createProductInvalidRequest() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(null, null, "image-url", "category", 0, 0);
        UserDTO user = buildDefaultUser();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);

        // For void methods, use doThrow() to mock exceptions
        doThrow(InvalidRequestException.class).when(productService).createProduct(any(), any());

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isBadRequest());
    }

}
