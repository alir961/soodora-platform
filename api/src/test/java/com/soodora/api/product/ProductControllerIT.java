package com.soodora.api.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.Currency;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductFilter;
import com.soodora.domain.user.UserRepository;
import com.soodora.service.auth.UserPrincipal;
import com.soodora.service.common.InvalidRequestException;
import com.soodora.service.product.CreateProductRequest;
import com.soodora.service.product.ProductService;
import com.soodora.service.product.UpdateProductRequest;

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

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static com.soodora.api.JsonStringify.asJsonString;
import static com.soodora.test.TestConstants.ARTIST_USER;
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
    void givenArtistUser_whenCreateProduct_thenSuccess() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(
                "Product Name",
                "Description",
                "category",
                BigDecimal.valueOf(100.99),
                Currency.EUR,
                10);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(request).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes());

        when(userRepository.find(ARTIST_USER.getIdentifier())).thenReturn(ARTIST_USER);
        doNothing().when(productService).createProduct(any(), eq(ARTIST_USER), any());

        // WHEN & THEN
        mockMvc.perform(multipart("/products")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(ARTIST_USER))))
                .andExpect(status().isCreated());
    }

    @Test
    void givenInvalidRequest_whenCreateProduct_thenInvalidRequest() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(
                null,
                null,
                "category",
                BigDecimal.valueOf(0.0),
                Currency.EUR,
                0);

        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");

        when(userRepository.find(ARTIST_USER.getIdentifier())).thenReturn(ARTIST_USER);
        doThrow(InvalidRequestException.class).when(productService)
                .createProduct(any(), eq(ARTIST_USER), eq(fileData));

        // WHEN & THEN
        mockMvc.perform(post("/products")
                .with(user(new UserPrincipal(ARTIST_USER)))
                .contentType(APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNoArtistUser_whenCreateProduct_thenForbidden() throws Exception {
        // GIVEN
        CreateProductRequest request = new CreateProductRequest(
                "Product Name",
                "Description",
                "category",
                BigDecimal.valueOf(100.99),
                Currency.EUR,
                10);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(request).getBytes());

        // WHEN & THEN
        mockMvc.perform(multipart("/products")
                .file(dataPart)
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenValidPage_whenGetProducts_thenSuccess() throws Exception {
        // GIVEN
        Page<ProductDTO> productsPage = new PageImpl<>(List.of(A_PRODUCT));
        when(productService.retrieveProducts(any(ProductFilter.class), any(Pageable.class))).thenReturn(productsPage);

        // WHEN & THEN
        mockMvc.perform(get("/products/search")
                .with(user(new UserPrincipal(USER)))
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].slug").value(A_PRODUCT.slug()))
                .andExpect(jsonPath("$.content[0].name").value(A_PRODUCT.name()));
    }

    @Test
    void givenValidSlug_whenGetProduct_thenSuccess() throws Exception {
        // GIVEN
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        when(productService.retrieveProduct("slug")).thenReturn(A_PRODUCT);

        mockMvc.perform(get("/products/slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidRequest_whenUpdateProduct_thenSuccess() throws Exception {
        // GIVEN
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Product Name",
                "Updated Description",
                "Updated Category",
                BigDecimal.valueOf(10.99),
                Currency.EUR,
                20L);

        MockMultipartFile jsonPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(request).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "updated-image.jpg",
                "image/jpeg",
                "dummy updated image".getBytes());

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);

        // WHEN & THEN
        mockMvc.perform(multipart("/products/productSlug1")
                .file(jsonPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER)))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isOk());
    }

    @Test
    void givenInvalidRequest_whenUpdateProduct_thenInvalidRequest() throws Exception {
        // GIVEN
        UpdateProductRequest updateProductRequest = new UpdateProductRequest(
                "",
                "",
                "",
                BigDecimal.valueOf(100.99),
                Currency.EUR,
                0L);

        MockMultipartFile dataPart = new MockMultipartFile(
                "data",
                "",
                "application/json",
                asJsonString(updateProductRequest).getBytes());

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test-image.jpg",
                "image/jpeg",
                "dummy image content".getBytes());

        FileData fileData = new FileData(filePart.getBytes(), filePart.getOriginalFilename(),
                filePart.getContentType());

        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doThrow(new InvalidRequestException("Invalid request"))
                .when(productService).updateProduct(any(), eq("productSlug1"), eq(fileData));

        // WHEN & THEN
        mockMvc.perform(multipart("/products/productSlug1")
                .file(dataPart)
                .file(filePart)
                .with(user(new UserPrincipal(USER)))
                .with(requestPostProcessor -> {
                    requestPostProcessor.setMethod("PUT");
                    return requestPostProcessor;
                }))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenValidSlug_whenDeleteProduct_thenSuccess() throws Exception {
        // GIVEN
        when(userRepository.find(USER.getIdentifier())).thenReturn(USER);
        doNothing().when(productService).deleteProductBySlug("slug");

        // WHEN & THEN
        mockMvc.perform(delete("/products/slug")
                .with(user(new UserPrincipal(USER))))
                .andExpect(status().isOk());
        verify(productService).deleteProductBySlug("slug");
    }

}
