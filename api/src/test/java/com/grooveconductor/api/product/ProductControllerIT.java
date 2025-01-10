package com.grooveconductor.api.product;

import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.product.UpdateProductCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.auth.JwtTokenRepository;
import com.grooveconductor.service.auth.AuthenticationService;
import com.grooveconductor.service.common.InvalidRequestException;
import com.grooveconductor.service.product.CreateProductRequest;
import com.grooveconductor.service.product.ProductService;
import com.grooveconductor.service.product.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.grooveconductor.api.JsonStringify.asJsonString;
import static com.grooveconductor.test.factory.ProductFactory.buildDefaultProduct;
import static com.grooveconductor.test.factory.UserFactory.buildDefaultUser;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    private JwtTokenRepository jwtTokenService;

    private static final ProductDTO A_PRODUCT = buildDefaultProduct();

    @Test
    void createProduct_Success() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest("Product Name", "Description",
            "image-url", "category", 100, 10);
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

    @Test
    void getProducts_Success() throws Exception {
        when(productService.retrieveAllProducts()).thenReturn(Collections.singletonList(A_PRODUCT));

        mockMvc.perform(get("/products"))
            .andExpect(status().isOk());
    }

    @Test
    void getProduct_Success() throws Exception {
        when(productService.retrieveProduct("product-slug")).thenReturn(A_PRODUCT);
        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user@example.com");

        mockMvc.perform(get("/products/product-slug")
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_Success() throws Exception {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest(
            "Updated Product Name",
            "Updated Description",
            "image",
            "Updated Category",
            10L,
            20L);

        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");

        mockMvc.perform(put("/products/productSlug1")
                .contentType("application/json")
                .content(asJsonString(updateProductRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isAccepted());
    }

    @Test
    void updateProduct_InvalidRequest() throws Exception {
        UpdateProductRequest updateProductRequest = new UpdateProductRequest(
            "",
            "",
            "",
            "",
            0L,
            0L);

        when(jwtTokenService.getEmailFromToken(anyString())).thenReturn("user1@example.com");
        doThrow(new InvalidRequestException("Invalid request"))
            .when(productService).updateProduct(any(UpdateProductCommand.class), eq("productSlug1"));

        mockMvc.perform(put("/products/productSlug1")
                .contentType("application/json")
                .content(asJsonString(updateProductRequest))
                .header("Authorization", "Bearer token"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.reason").value("Invalid request"));
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

}
