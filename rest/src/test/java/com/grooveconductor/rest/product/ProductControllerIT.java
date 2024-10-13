package com.grooveconductor.rest.product;

import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.application.common.InvalidRequestException;
import com.grooveconductor.application.product.CreateProductRequest;
import com.grooveconductor.application.auth.AuthenticationService;
import com.grooveconductor.application.auth.JwtTokenService;
import com.grooveconductor.application.product.ProductService;
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

    @MockBean
    private JwtTokenService jwtTokenService;

    @Test
    void getProducts_Success() throws Exception {
        // GIVEN
        ProductDTO product = buildDefaultProduct();
        when(productService.retrieveAllProducts()).thenReturn(Collections.singletonList(product));

        // WHEN & THEN
        mockMvc.perform(get("/products"))
            .andExpect(status().isOk());
    }

    @Test
    void getProduct_Success() throws Exception {
        // GIVEN
        ProductDTO product = buildDefaultProduct();
        when(productService.retrieveProduct("product-slug")).thenReturn(product);
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user@example.com");

        // WHEN & THEN
        mockMvc.perform(get("/products/product-slug")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProduct_Success() throws Exception {
        // GIVEN
        doNothing().when(productService).deleteProductBySlug("product-slug");
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        // WHEN & THEN
        mockMvc.perform(delete("/products/product-slug")
                .header("Authorization", "Bearer token"))
            .andExpect(status().isOk());
        verify(productService, times(1)).deleteProductBySlug("product-slug");
    }

    @Test
    void createProduct_Success() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest("Product Name", "Description", "image-url", "category", 100, 10);
        UserDTO user = buildDefaultUser();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user@example.com");

        doNothing().when(productService).createProduct(any(), any());

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .header("Authorization", "Bearer token")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isCreated());
    }


    @Test
    void createProduct_InvalidRequest() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(null, null, "image-url", "category", 0, 0);
        UserDTO user = buildDefaultUser();

        when(authenticationService.getAuthenticatedUser()).thenReturn(user);
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user@example.com");

        doThrow(InvalidRequestException.class).when(productService).createProduct(any(), any());

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .header("Authorization", "Bearer token")
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
            .andExpect(status().isBadRequest());
    }

}
