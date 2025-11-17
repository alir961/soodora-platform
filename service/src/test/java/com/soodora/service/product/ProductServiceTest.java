package com.soodora.service.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.Currency;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;

import java.math.BigDecimal;
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

import static com.soodora.domain.factory.ProductFactory.buildProduct;
import static com.soodora.test.TestConstants.USER;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    private static final Product PRODUCT_1 = buildProduct("1");
    private static final Product PRODUCT_2 = buildProduct("2");
    private static final Product PRODUCT_3 = buildProduct("3");
    private static final String SLUG_1 = "slug1";
    private static final Pageable PAGEABLE = PageRequest.of(0, 50, Sort.by("slug").ascending());
    private static final List<Product> PRODUCTS = List.of(PRODUCT_1, PRODUCT_2, PRODUCT_3);
    private static final Page<Product> PRODUCTS_PAGE = new PageImpl<>(PRODUCTS, PAGEABLE, PRODUCTS.size());
    private static final CreateProductCommand COMMAND = new CreateProductCommand(
            PRODUCT_1.getName(), PRODUCT_1.getDescription(), PRODUCT_1.getCategory(), PRODUCT_1.getPrice().amount(),
            PRODUCT_1.getPrice().currency(), PRODUCT_1.getQuantity());

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void retrieveAllProducts_Success() {
        // GIVEN
        when(productRepository.findAll(PAGEABLE)).thenReturn(PRODUCTS_PAGE);

        // WHEN
        Page<Product> retrievedProducts = productService.retrieveAllProducts(PAGEABLE);

        // THEN
        assertEquals(PRODUCTS, retrievedProducts.getContent());
    }

    @Test
    void retrieveProducts_FilteredByUserSlug() {
        // GIVEN
        ProductFilter filter = new ProductFilter(of(USER.getSlug()), empty(), empty(), empty());

        List<Product> filteredProducts = List.of(PRODUCT_1, PRODUCT_3);
        Page<Product> pageFilteredProducts = new PageImpl<>(filteredProducts, PAGEABLE, filteredProducts.size());

        when(productRepository.findByFilter(filter, PAGEABLE)).thenReturn(pageFilteredProducts);

        // WHEN
        Page<Product> retrievedProducts = productService.retrieveProducts(filter, PAGEABLE);

        // THEN
        assertEquals(2, retrievedProducts.getContent().size());
        assertEquals(PRODUCT_1, retrievedProducts.getContent().get(0));
        assertEquals(PRODUCT_3, retrievedProducts.getContent().get(1));
    }

    @Test
    void retrieveProduct_Success() {
        // GIVEN
        when(productRepository.findBySlug(PRODUCT_1.getSlug())).thenReturn(PRODUCT_1);

        // WHEN
        Product retrievedProduct = productService.retrieveProduct(PRODUCT_1.getSlug());

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
        assertThrows(UnknownProductException.class, executable);
    }

    @Test
    void updateProduct_Success() {
        UpdateProductCommand updateCommand = new UpdateProductCommand(
                "Updated Name",
                "Updated Description",
                "newCategory",
                BigDecimal.valueOf(149.99),
                Currency.EUR,
                5L);
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");
        when(productRepository.update(updateCommand, SLUG_1, fileData)).thenReturn(PRODUCT_1);

        Product updatedProduct = productService.updateProduct(updateCommand, SLUG_1, fileData);

        assertEquals(PRODUCT_1, updatedProduct);
        verify(productRepository).update(any(UpdateProductCommand.class), eq(SLUG_1), eq(fileData));
    }

    @Test
    void updateProduct_UnknownSlug() {
        UpdateProductCommand updateCommand = new UpdateProductCommand(
                "Updated Name",
                "Updated Description",
                "newCategory",
                BigDecimal.valueOf(149.99),
                Currency.EUR,
                5L);
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");
        when(productRepository.update(updateCommand, SLUG_1, fileData))
                .thenThrow(new UnknownSlugException("Slug not found"));

        assertThrows(ProductUnityException.class, () -> productService.updateProduct(updateCommand, SLUG_1, fileData));
    }

    @Test
    void deleteProduct_Success() {
        // GIVEN
        doNothing().when(productRepository).deleteProductBySlug(PRODUCT_1.getSlug());

        // WHEN
        Executable executable = () -> productService.deleteProductBySlug(PRODUCT_1.getSlug());

        // THEN
        assertDoesNotThrow(executable);
        verify(productRepository).deleteProductBySlug(PRODUCT_1.getSlug());
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
        when(productRepository.isProductNameUsedByUser(COMMAND.name(), USER.getSlug())).thenReturn(false);
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");

        // WHEN
        Executable executable = () -> productService.createProduct(COMMAND, USER, fileData);

        // THEN
        assertDoesNotThrow(executable);
        verify(productRepository).save(any(), eq(USER), eq(fileData));
    }

    @Test
    void createProduct_NameAlreadyUsed() {
        when(productRepository.isProductNameUsedByUser(COMMAND.name(), USER.getSlug())).thenReturn(true);
        FileData fileData = new FileData(
                "dummy image content".getBytes(),
                "test-image.jpg",
                "image/jpeg");

        // WHEN
        Executable executable = () -> productService.createProduct(COMMAND, USER, fileData);

        // THEN
        assertThrows(ProductUnityException.class, executable);
    }
}
