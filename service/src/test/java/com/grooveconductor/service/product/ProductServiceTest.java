package com.grooveconductor.service.product;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.product.*;
import com.grooveconductor.service.user.UnknownUserException;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.grooveconductor.test.TestConstants.USER;
import static com.grooveconductor.test.factory.ProductFactory.buildProduct;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private static final ProductDTO PRODUCT_1 = buildProduct("1");
    private static final ProductDTO PRODUCT_2 = buildProduct("2");
    private static final ProductDTO PRODUCT_3 = buildProduct("3");
    private static final String SLUG_1 = "slug1";
    private static final Pageable PAGEABLE = PageRequest.of(0, 50, Sort.by("slug").ascending());
    private static final List<ProductDTO> PRODUCTS = List.of(PRODUCT_1, PRODUCT_2, PRODUCT_3);
    private static final Page<ProductDTO> PRODUCTS_PAGE = new PageImpl<>(PRODUCTS, PAGEABLE, PRODUCTS.size());
    private static final CreateProductCommand COMMAND = new CreateProductCommand(
            PRODUCT_1.name(), PRODUCT_1.description(), PRODUCT_1.imageUrl(), PRODUCT_1.category(), PRODUCT_1.price(),
            PRODUCT_1.quantity());

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void retrieveAllProducts_Success() {
        // GIVEN
        when(productRepository.findAll(PAGEABLE)).thenReturn(PRODUCTS_PAGE);

        // WHEN
        Page<ProductDTO> retrievedProducts = productService.retrieveAllProducts(PAGEABLE);

        // THEN
        assertEquals(PRODUCTS, retrievedProducts.getContent());
    }

    @Test
    void retrieveProducts_FilteredByUserSlug() {
        // GIVEN
        ProductFilter filter = new ProductFilter(of(USER.slug()), empty(), empty(), empty());

        List<ProductDTO> filteredProducts = List.of(PRODUCT_1, PRODUCT_3);
        Page<ProductDTO> pageFilteredProducts = new PageImpl<>(filteredProducts, PAGEABLE, filteredProducts.size());

        when(productRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageFilteredProducts);

        // WHEN
        Page<ProductDTO> retrievedProducts = productService.retrieveProducts(filter, PAGEABLE);

        // THEN
        assertEquals(2, retrievedProducts.getContent().size());
        assertEquals(PRODUCT_1, retrievedProducts.getContent().get(0));
        assertEquals(PRODUCT_3, retrievedProducts.getContent().get(1));
    }

    @Test
    void retrieveProduct_Success() {
        // GIVEN
        when(productRepository.findBySlug(PRODUCT_1.slug())).thenReturn(PRODUCT_1);

        // WHEN
        ProductDTO retrievedProduct = productService.retrieveProduct(PRODUCT_1.slug());

        // THEN
        assertEquals(PRODUCT_1, retrievedProduct);
    }

    @Test
    void retrieveProduct_UnknownSlug() {
        // GIVEN
        when(productRepository.findBySlug(anyString())).thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> productService.retrieveProduct("unknown-slug");

        // THEN
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void updateProduct_Success() {
        UpdateProductCommand updateCommand = new UpdateProductCommand(
                "Updated Name",
                "Updated Description",
                "newImageUrl",
                "newCategory",
                150L,
                5L);

        when(productRepository.update(updateCommand, SLUG_1)).thenReturn(PRODUCT_1);

        ProductDTO updatedProduct = productService.updateProduct(updateCommand, SLUG_1);

        assertEquals(PRODUCT_1, updatedProduct);
        verify(productRepository).update(any(UpdateProductCommand.class), eq(SLUG_1));
    }

    @Test
    void updateProduct_UnknownSlug() {
        UpdateProductCommand updateCommand = new UpdateProductCommand(
                "Updated Name",
                "Updated Description",
                "newImageUrl",
                "newCategory",
                150L,
                5L);

        when(productRepository.update(updateCommand, SLUG_1)).thenThrow(new UnknownSlugException("Slug not found"));

        assertThrows(ProductUnityException.class, () -> {
            productService.updateProduct(updateCommand, SLUG_1);
        });
    }

    @Test
    void deleteProduct_Success() {
        // GIVEN
        doNothing().when(productRepository).deleteProductBySlug(PRODUCT_1.slug());

        // WHEN
        Executable executable = () -> productService.deleteProductBySlug(PRODUCT_1.slug());

        // THEN
        assertDoesNotThrow(executable);
        verify(productRepository).deleteProductBySlug(PRODUCT_1.slug());
    }

    @Test
    void deleteProduct_UnknownSlug() {
        // GIVEN
        doThrow(UnknownSlugException.class).when(productRepository).deleteProductBySlug(anyString());

        // WHEN
        Executable executable = () -> productService.deleteProductBySlug("unknown-slug");

        // THEN
        assertThrows(UnknownProductException.class, executable);
    }

    @Test
    void createProduct_Success() {
        // GIVEN
        when(productRepository.isProductNameUsedByUser(COMMAND.name(), USER.slug())).thenReturn(false);

        // WHEN
        Executable executable = () -> productService.createProduct(COMMAND, USER);

        // THEN
        assertDoesNotThrow(executable);
        verify(productRepository).save(any(), eq(USER));
    }

    @Test
    void createProduct_NameAlreadyUsed() {
        when(productRepository.isProductNameUsedByUser(COMMAND.name(), USER.slug())).thenReturn(true);

        // WHEN
        Executable executable = () -> productService.createProduct(COMMAND, USER);

        // THEN
        assertThrows(ProductUnityException.class, executable);
    }
}
