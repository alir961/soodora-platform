package com.soodora.api.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.product.*;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.product.CreateProductRequest;
import com.soodora.service.product.ProductService;
import com.soodora.service.product.UnknownProductException;
import com.soodora.service.product.UpdateProductRequest;
import com.soodora.service.shared.InvalidPermissionException;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static java.time.Duration.ZERO;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.domain.common.Currency.EUR;
import static com.soodora.test.TestConstants.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private static final Page<Product> PRODUCT_PAGE = new PageImpl<>(List.of(PRODUCT));

    private static final MockMultipartFile FILE_DATA = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "dummy image content".getBytes());

    private static final CreateProductRequest VALID_CREATE_REQUEST = new CreateProductRequest(
            "Product Name",
            "Description",
            "category",
            BigDecimal.valueOf(100.99),
            EUR,
            10);
    private static final UpdateProductRequest VALID_UPDATE_REQUEST = new UpdateProductRequest(
            "Updated Product Name",
            "Updated Description",
            "Updated Category",
            BigDecimal.valueOf(10.99),
            EUR,
            20L);

    private static final MockMultipartFile CREATE_REQUEST_JSON = new MockMultipartFile(
            "data",
            "",
            "application/json",
            asJsonString(VALID_CREATE_REQUEST).getBytes());
    private static final MockMultipartFile UPDATE_REQUEST_JSON = new MockMultipartFile(
            "data",
            "",
            "application/json",
            asJsonString(VALID_UPDATE_REQUEST).getBytes());

    @Test
    void givenValidRequest_whenCreateProduct_thenCreated() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/products")
                .file(CREATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidRequest_whenCreateProduct_thenBadRequest() throws Exception {
        // GIVEN
        CreateProductRequest invalidRequest = new CreateProductRequest(
                null,
                null,
                "category",
                BigDecimal.valueOf(0.0),
                EUR,
                0);

        doThrow(InvalidRequestException.class).when(productService)
                .createProduct(any(CreateProductCommand.class), eq(ARTIST_USER), any(FileData.class));

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenAuthenticatedUser_whenCreateProduct_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/products")
                .file(CREATE_REQUEST_JSON)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenCreateProduct_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/products")
                .file(CREATE_REQUEST_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPage_whenSearchProducts_thenOk() throws Exception {
        // GIVEN
        when(productService.retrieveProducts(any(ProductFilter.class), any(Pageable.class)))
                .thenReturn(PRODUCT_PAGE);

        // WHEN & THEN
        mockMvc.perform(get("/products/search")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value(PRODUCT.getSlug()));
    }

    @Test
    void givenValidSlug_whenGetProduct_thenOk() throws Exception {
        // GIVEN
        when(productService.retrieveProduct("slug")).thenReturn(PRODUCT);

        mockMvc.perform(get("/products/slug"))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenGetProduct_thenNotFound() throws Exception {
        // GIVEN
        when(productService.retrieveProduct("slug"))
                .thenThrow(new UnknownProductException("Unable to find product"));

        mockMvc.perform(get("/products/slug"))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenValidRequest_whenUpdateProduct_thenOk() throws Exception {
        // GIVEN
        when(productService.updateProduct(eq(ARTIST_USER), eq(PRODUCT.getSlug()), any(), any()))
                .thenReturn(PRODUCT);

        // WHEN & THEN
        mockMvc.perform(multipart("/products/" + PRODUCT.getSlug())
                .file(UPDATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateProduct_thenBadRequest() throws Exception {
        // GIVEN
        UpdateProductRequest invalidRequest = new UpdateProductRequest(
                "",
                "",
                "",
                BigDecimal.valueOf(100.99),
                EUR,
                0L);
        MockMultipartFile requestJsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(invalidRequest).getBytes());

        doThrow(new InvalidRequestException("Invalid request"))
                .when(productService)
                .updateProduct(eq(ARTIST_USER), eq("slug"), any(UpdateProductCommand.class), any(FileData.class));

        // WHEN & THEN
        mockMvc.perform(multipart("/products/slug")
                .file(requestJsonPart)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenInvalidPermission_whenUpdateProduct_thenForbidden() throws Exception {
        // GIVEN
        when(productService.updateProduct(eq(ARTIST_USER), eq(PRODUCT.getSlug()), any(), any()))
                .thenThrow(new InvalidPermissionException("Only the owner can update the product"));

        // WHEN & THEN
        mockMvc.perform(multipart("/products/" + PRODUCT.getSlug())
                .file(UPDATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(ARTIST_USER)))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenUpdateProduct_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/products/" + PRODUCT.getSlug())
                .file(UPDATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(user(new UserPrincipal(USER)))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenUpdateProduct_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(multipart("/products/" + PRODUCT.getSlug())
                .file(UPDATE_REQUEST_JSON)
                .file(FILE_DATA)
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenDeleteProduct_thenOk() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/products/slug")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidPermission_whenDeleteProduct_thenForbidden() throws Exception {
        // GIVEN
        doThrow(new InvalidPermissionException("Only the owner can delete the product"))
                .when(productService).deleteProduct(ARTIST_USER, "slug");

        // WHEN & THEN
        mockMvc.perform(delete("/products/slug")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenDeleteProduct_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/products/slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenDeleteProduct_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(delete("/products/slug"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetGlobalAnalytics_thenOk() throws Exception {
        // GIVEN
        when(productService.computeGlobalAnalytics(ARTIST_USER))
                .thenReturn(ProductAnalytics.empty());

        // WHEN & THEN
        mockMvc.perform(get("/products/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenAuthenticatedUser_whenGetGlobalAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/products/analytics")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenGetGlobalAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/products/analytics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidSlug_whenGetProductAnalytics_thenOk() throws Exception {
        // GIVEN
        ProductAnalytics analytics = new ProductAnalytics(0, 0, 0, 0, ZERO, "$");

        when(productService.computeProductAnalytics(ARTIST_USER, "slug"))
                .thenReturn(analytics);

        // WHEN & THEN
        mockMvc.perform(get("/products/slug/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidSlug_whenGetProductAnalytics_thenNotFound() throws Exception {
        // GIVEN
        when(productService.computeProductAnalytics(ARTIST_USER, "slug"))
                .thenThrow(new UnknownProductException("Unable to find product"));

        // WHEN & THEN
        mockMvc.perform(get("/products/slug/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInvalidPermission_whenGetProductAnalytics_thenForbidden() throws Exception {
        // GIVEN
        when(productService.computeProductAnalytics(ARTIST_USER, "slug"))
                .thenThrow(new InvalidPermissionException("Only the owner can get analytics"));

        // WHEN & THEN
        mockMvc.perform(get("/products/slug/analytics")
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAuthenticatedUser_whenGetProductAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/products/slug/analytics")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUnauthenticatedUser_whenGetProductAnalytics_thenForbidden() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/products/slug/analytics"))
                .andExpect(status().isForbidden());
    }

}
