package com.soodora.api.product;

import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductFilter;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.product.CreateProductRequest;
import com.soodora.service.product.ProductService;
import com.soodora.service.product.UpdateProductRequest;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.USER;
import static com.soodora.test.factory.ProductFactory.buildDefaultProduct;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private UserRepository userRepository;

    private static final ProductDTO A_PRODUCT = buildDefaultProduct();

    @Test
    void createProduct_Success() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(
                "Product Name",
                "Description",
                "image-url",
                "category",
                100,
                10);

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(productService).createProduct(any(), eq(USER));

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .with(user(new UserPrincipal(USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void createProduct_InvalidRequest() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(
                null,
                null,
                "image-url",
                "category",
                0,
                0);

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(InvalidRequestException.class).when(productService)
                .createProduct(any(), eq(USER));

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .with(user(new UserPrincipal(USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProducts_Success() throws Exception {
        // GIVEN
        Page<ProductDTO> productsPage = new PageImpl<>(List.of(A_PRODUCT));
        when(productService.retrieveProducts(any(ProductFilter.class), any(Pageable.class))).thenReturn(productsPage);

        // WHEN & THEN
        mockMvc.perform(get("/products/search")
                .with(user(new UserPrincipal(USER))) // if your endpoint requires auth like /posts example
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value(A_PRODUCT.slug()))
                .andExpect(jsonPath("$.content[0].name").value(A_PRODUCT.name()));
    }

    @Test
    void getProduct_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        when(productService.retrieveProduct("product-slug")).thenReturn(A_PRODUCT);

        mockMvc.perform(get("/products/product-slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void updateProduct_Success() throws Exception {
        // GIVEN
        UpdateProductRequest updateProductRequest = new UpdateProductRequest(
                "Updated Product Name",
                "Updated Description",
                "image",
                "Updated Category",
                10L,
                20L);

        when(userRepository.find(USER.identifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(put("/products/productSlug1")
                .contentType("application/json")
                .content(asJsonString(updateProductRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isAccepted());
    }

    @Test
    void updateProduct_InvalidRequest() throws Exception {
        // GIVEN
        UpdateProductRequest updateProductRequest = new UpdateProductRequest(
                "",
                "",
                "",
                "",
                0L,
                0L);

        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request"))
                .when(productService).updateProduct(any(), eq("productSlug1"));

        mockMvc.perform(put("/products/productSlug1")
                .contentType("application/json")
                .content(asJsonString(updateProductRequest))
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.reason").value("Invalid request"));
    }

    @Test
    void deleteProduct_Success() throws Exception {
        // GIVEN
        when(userRepository.find(USER.identifier())).thenReturn(USER);
        doNothing().when(productService).deleteProductBySlug("product-slug");

        // WHEN & THEN
        mockMvc.perform(delete("/products/product-slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(productService).deleteProductBySlug("product-slug");
    }

}
