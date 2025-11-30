package com.soodora.service.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.common.Currency;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;
import com.soodora.service.shared.InvalidPermissionException;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static com.soodora.domain.factory.ProductFactory.buildProduct;
import static com.soodora.test.TestConstants.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private static final Product PRODUCT_1 = buildProduct("1");
    private static final Product PRODUCT_2 = buildProduct("2");
    private static final Product PRODUCT_3 = buildProduct("3");
    private static final String SLUG_1 = "slug1";
    private static final Pageable PAGEABLE = PageRequest.of(0, 50, Sort.by("slug").ascending());
    private static final List<Product> PRODUCTS = List.of(PRODUCT_1, PRODUCT_2, PRODUCT_3);
    private static final Page<Product> PRODUCTS_PAGE = new PageImpl<>(PRODUCTS, PAGEABLE, PRODUCTS.size());

    private static final CreateProductCommand VALID_CREATE_COMMAND = new CreateProductCommand(
            PRODUCT_1.getName(), PRODUCT_1.getDescription(), PRODUCT_1.getCategory(), PRODUCT_1.getPrice().amount(),
            PRODUCT_1.getPrice().currency(), PRODUCT_1.getQuantity());
    private static final UpdateProductCommand VALID_UPDATE_COMMAND = new UpdateProductCommand(
            "Updated Name",
            "Updated Description",
            "newCategory",
            BigDecimal.valueOf(149.99),
            Currency.EUR,
            5L);

    private static final FileData FILE_DATA = new FileData(
            "dummy image content".getBytes(),
            "test-image.jpg",
            "image/jpeg");

    @Test
    void givenValidProduct_whenCreateProduct_thenProductIsSaved() {
        // GIVEN
        when(productRepository.isProductNameUsedByUser(VALID_CREATE_COMMAND.name(), USER.getSlug()))
                .thenReturn(false);

        // WHEN
        productService.createProduct(VALID_CREATE_COMMAND, USER, FILE_DATA);

        // THEN
        verify(productRepository)
                .save(VALID_CREATE_COMMAND, USER, FILE_DATA);
    }

    @Test
    void givenUsedProductName_whenCreateProduct_thenThrowProductUnityException() {
        when(productRepository.isProductNameUsedByUser(VALID_CREATE_COMMAND.name(), USER.getSlug()))
                .thenReturn(true);

        // WHEN
        Executable executable = () -> productService.createProduct(VALID_CREATE_COMMAND, USER, FILE_DATA);

        // THEN
        assertThrows(ProductUnityException.class, executable);
    }

    @Test
    void givenPage_whenRetrieveAllProducts_thenReturnPageOfProducts() {
        // GIVEN
        when(productRepository.findAll(PAGEABLE))
                .thenReturn(PRODUCTS_PAGE);

        // WHEN
        Page<Product> retrievedProducts = productService.retrieveAllProducts(PAGEABLE);

        // THEN
        assertEquals(PRODUCTS, retrievedProducts.getContent());
    }

    @Test
    void givenEmptyFilter_whenRetrieveProducts_thenReturnAllProducts() {
        // GIVEN
        ProductFilter filter = new ProductFilter(empty(), empty(), empty(), empty());
        Page<Product> pageFilteredProducts = new PageImpl<>(PRODUCTS, PAGEABLE, PRODUCTS.size());

        when(productRepository.findAll(PAGEABLE))
                .thenReturn(pageFilteredProducts);

        // WHEN
        List<Product> retrievedProducts = productService.retrieveProducts(filter, PAGEABLE)
                .getContent();

        // THEN
        assertEquals(3, retrievedProducts.size());
        assertEquals(PRODUCT_1, retrievedProducts.get(0));
        assertEquals(PRODUCT_2, retrievedProducts.get(1));
        assertEquals(PRODUCT_3, retrievedProducts.get(2));
    }

    @Test
    void givenUserFilter_whenRetrieveProducts_thenReturnFilteredProducts() {
        // GIVEN
        ProductFilter filter = new ProductFilter(of(USER.getSlug()), empty(), empty(), empty());
        List<Product> filteredProducts = List.of(PRODUCT_1, PRODUCT_3);
        Page<Product> pageFilteredProducts = new PageImpl<>(filteredProducts, PAGEABLE, filteredProducts.size());

        when(productRepository.findByFilter(filter, PAGEABLE))
                .thenReturn(pageFilteredProducts);

        // WHEN
        Page<Product> retrievedProducts = productService.retrieveProducts(filter, PAGEABLE);

        // THEN
        assertEquals(2, retrievedProducts.getContent().size());
        assertEquals(PRODUCT_1, retrievedProducts.getContent().get(0));
        assertEquals(PRODUCT_3, retrievedProducts.getContent().get(1));
    }

    @Test
    void givenValidSlug_whenRetrieveProduct_thenReturnProduct() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        Product retrievedProduct = productService.retrieveProduct(SLUG_1);

        // THEN
        assertEquals(PRODUCT_1, retrievedProduct);
    }

    @Test
    void givenInvalidSlug_whenRetrieveProduct_thenThrowUnknownProductException() {
        // GIVEN
        when(productRepository.findBySlug("unknown"))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> productService.retrieveProduct("unknown");

        // THEN
        assertThrows(UnknownProductException.class, executable);
    }

    @Test
    void givenProductVendor_whenUpdateProduct_thenReturnUpdatedProduct() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);
        when(productRepository.update(VALID_UPDATE_COMMAND, SLUG_1, FILE_DATA))
                .thenReturn(PRODUCT_1);

        // WHEN
        Product updatedProduct = productService.updateProduct(ARTIST_USER, SLUG_1, VALID_UPDATE_COMMAND, FILE_DATA);

        // THEN
        assertEquals(PRODUCT_1, updatedProduct);
    }

    @Test
    void givenAdministrator_whenUpdateProduct_thenReturnUpdatedProduct() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);
        when(productRepository.update(VALID_UPDATE_COMMAND, SLUG_1, FILE_DATA))
                .thenReturn(PRODUCT_1);

        // WHEN
        Product updatedProduct = productService.updateProduct(ADMIN, SLUG_1, VALID_UPDATE_COMMAND, FILE_DATA);

        // THEN
        assertEquals(PRODUCT_1, updatedProduct);
    }

    @Test
    void givenInvalidUser_whenUpdateProduct_thenThrowInvalidPermissionException() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        Executable executable = () -> productService.updateProduct(USER, SLUG_1, VALID_UPDATE_COMMAND, FILE_DATA);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the vendor can update the product");
    }

    @Test
    void givenInvalidSlug_whenUpdateProduct_thenThrowUnknownProductException() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenThrow(UnknownSlugException.class);

        // WHEN
        Executable executable = () -> productService.updateProduct(ARTIST_USER, SLUG_1, VALID_UPDATE_COMMAND,
                FILE_DATA);

        // THEN
        assertThrows(UnknownProductException.class, executable);
    }

    @Test
    void givenProductVendor_whenDeleteProduct_thenDeleteProduct() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        productService.deleteProduct(ARTIST_USER, SLUG_1);

        // THEN
        verify(productRepository).deleteProductBySlug(SLUG_1);
    }

    @Test
    void givenAdministrator_whenDeleteProduct_thenDeleteProduct() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        productService.deleteProduct(ADMIN, SLUG_1);

        // THEN
        verify(productRepository).deleteProductBySlug(SLUG_1);
    }

    @Test
    void givenInvalidUser_whenDeleteProduct_thenThrowInvalidPermissionException() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        Executable executable = () -> productService.deleteProduct(USER, SLUG_1);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the vendor can delete the product");
    }

    @Test
    void givenInvalidSlug_whenDeleteProduct_thenThrowUnknownProductException() {
        // GIVEN
        doThrow(UnknownSlugException.class)
                .when(productRepository).findBySlug("unknown");

        // WHEN
        Executable executable = () -> productService.deleteProduct(ARTIST_USER, "unknown");

        // THEN
        assertThrows(UnknownProductException.class, executable);
    }

    @Test
    void givenProductVendor_whenComputeProductAnalytics_thenReturnAnalytics() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        ProductAnalytics analytics = productService.computeProductAnalytics(ARTIST_USER, SLUG_1);

        // THEN
        assertNotNull(analytics);
    }

    @Test
    void givenAdministrator_whenComputeProductAnalytics_thenReturnAnalytics() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        ProductAnalytics analytics = productService.computeProductAnalytics(ADMIN, SLUG_1);

        // THEN
        assertNotNull(analytics);
    }

    @Test
    void givenInvalidUser_whenComputeProductAnalytics_thenThrowInvalidPermissionException() {
        // GIVEN
        when(productRepository.findBySlug(SLUG_1))
                .thenReturn(PRODUCT_1);

        // WHEN
        Executable executable = () -> productService.computeProductAnalytics(USER, SLUG_1);

        // THEN
        assertThrows(InvalidPermissionException.class, executable, "Only the product vendor can access the analytics");
    }

}
