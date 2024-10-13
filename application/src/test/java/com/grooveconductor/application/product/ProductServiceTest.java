package com.grooveconductor.application.product;

import com.grooveconductor.domain.product.CreateProductCommand;
import com.grooveconductor.domain.product.ProductDTO;
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
        when(productRepository.findProductsByUserSlug(USER.slug())).thenReturn(PRODUCTS);

        // WHEN
        List<ProductDTO> retrievedProducts = productService.retrieveAllProductsOfUserBySlug(USER.slug());

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
        assertThrows(UnknownUserException.class, executable);
    }

    @Test
    void createProduct_Success() {
        // GIVEN
        CreateProductCommand command = new CreateProductCommand(PRODUCT.name(), PRODUCT.description(), PRODUCT.imageUrl(),
            PRODUCT.category(), PRODUCT.price(), PRODUCT.quantity());

        when(productRepository.isProductNameUsedByUser(command.name(), USER.slug())).thenReturn(false);

        // WHEN
        Executable executable = () -> productService.createProduct(command, USER);

        // THEN
        assertDoesNotThrow(executable);
        verify(productRepository, times(1)).save(any(CreateProductCommand.class), any(UserDTO.class));
    }

    @Test
    void createProduct_NameAlreadyUsed() {
        // GIVEN
        CreateProductCommand command = new CreateProductCommand(PRODUCT.name(), PRODUCT.description(), PRODUCT.imageUrl(),
            PRODUCT.category(), PRODUCT.price(), PRODUCT.quantity());

        when(productRepository.isProductNameUsedByUser(command.name(), USER.slug())).thenReturn(true);

        // WHEN
        Executable executable = () -> productService.createProduct(command, USER);

        // THEN
        assertThrows(ProductUnityException.class, executable);
    }
}
