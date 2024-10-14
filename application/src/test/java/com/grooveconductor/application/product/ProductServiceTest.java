package com.grooveconductor.application.product;

import com.grooveconductor.domain.product.CreateProductCommand;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.product.ProductFilter;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.product.ProductRepository;
import com.grooveconductor.application.user.UnknownUserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.grooveconductor.test.ProductFactory.buildDefaultProduct;
import static com.grooveconductor.test.UserFactory.buildDefaultUser;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    private ProductService productService;

    private static final ProductDTO PRODUCT = buildDefaultProduct();
    private static final List<ProductDTO> PRODUCTS = List.of(PRODUCT);
    private static final UserDTO USER = buildDefaultUser();
    private static final CreateProductCommand COMMAND = new CreateProductCommand(
        PRODUCT.name(), PRODUCT.description(), PRODUCT.imageUrl(), PRODUCT.category(), PRODUCT.price(), PRODUCT.quantity()
    );

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Test
    void retrieveAllProducts_Success() {
        // GIVEN
        when(productRepository.findAll()).thenReturn(PRODUCTS);

        // WHEN
        List<ProductDTO> retrievedProducts = productService.retrieveAllProducts();

        // THEN
        assertEquals(PRODUCTS, retrievedProducts);
    }

    @Test
    void retrieveAllProductsOfUser_Success() {
        // GIVEN
        ProductFilter filter = new ProductFilter(Optional.of(USER.slug()));

        when(productRepository.findProductsByUserSlug(USER.slug())).thenReturn(PRODUCTS);

        // WHEN
        List<ProductDTO> retrievedProducts = productService.retrieveProducts(filter);

        // THEN
        assertEquals(PRODUCTS, retrievedProducts);
    }

    @Test
    void retrieveProduct_Success() {
        // GIVEN
        when(productRepository.findBySlug(PRODUCT.slug())).thenReturn(PRODUCT);

        // WHEN
        ProductDTO retrievedProduct = productService.retrieveProduct(PRODUCT.slug());

        // THEN
        assertEquals(PRODUCT, retrievedProduct);
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
    void deleteProduct_Success() {
        // GIVEN
        doNothing().when(productRepository).deleteProductBySlug(PRODUCT.slug());

        // WHEN
        Executable executable = () -> productService.deleteProductBySlug(PRODUCT.slug());

        // THEN
        assertDoesNotThrow(executable);
        verify(productRepository, times(1)).deleteProductBySlug(PRODUCT.slug());
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
        verify(productRepository, times(1)).save(any(), any());
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
