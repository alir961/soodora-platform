package com.grooveconductor.infrastructure.product;

import com.grooveconductor.infrastructure.user.UserJpaRepository;
import com.grooveconductor.infrastructure.InfrastructureTestConfiguration;
import com.grooveconductor.infrastructure.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static com.grooveconductor.infrastructure.product.ProductEntityFactory.buildDefaultProductEntity;
import static com.grooveconductor.infrastructure.user.UserEntityFactory.buildDefaultUserEntity;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfrastructureTestConfiguration.class)
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private UserEntity userEntity;
    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        userEntity = buildDefaultUserEntity();
        userEntity = userRepository.save(userEntity);

        productEntity = buildDefaultProductEntity(userEntity);
        productRepository.save(productEntity);
    }

    @Test
    void testFindBySlug_ProductExists() {
        // WHEN
        Optional<ProductEntity> foundProduct = productRepository.findBySlug("slug");

        // THEN
        assertTrue(foundProduct.isPresent());
        assertEquals("slug", foundProduct.get().getSlug());
    }

    @Test
    void testFindBySlug_ProductDoesNotExist() {
        // WHEN
        Optional<ProductEntity> foundProduct = productRepository.findBySlug("unknown-slug");

        // THEN
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindByName_ProductExists() {
        // WHEN
        Optional<ProductEntity> foundProduct = productRepository.findByName("Name");

        // THEN
        assertTrue(foundProduct.isPresent());
        assertEquals("Name", foundProduct.get().getName());
    }

    @Test
    void testFindByName_ProductDoesNotExist() {
        // WHEN
        Optional<ProductEntity> foundProduct = productRepository.findByName("Unknown Product");

        // THEN
        assertFalse(foundProduct.isPresent());
    }

    @Test
    void testFindProductsByUserSlug_ProductsExist() {
        // WHEN
        Optional<List<ProductEntity>> foundProducts = productRepository.findProductsByUserSlug("slug");

        // THEN
        assertTrue(foundProducts.isPresent());
        assertFalse(foundProducts.get().isEmpty());
    }

    @Test
    void testFindProductsByUserSlug_NoProductsExist() {
        // WHEN
        Optional<List<ProductEntity>> foundProducts = productRepository.findProductsByUserSlug("unknown-slug");

        // THEN
        assertTrue(foundProducts.isPresent());
        assertTrue(foundProducts.get().isEmpty());
    }

    @Test
    void testSaveProduct() {
        // GIVEN
        ProductEntity newProduct = buildDefaultProductEntity(userEntity);

        newProduct.setSlug("new-slug");

        // WHEN
        ProductEntity savedProduct = productRepository.save(newProduct);

        // THEN
        assertNotNull(savedProduct.getId());
        assertEquals("new-slug", savedProduct.getSlug());
    }

    @Test
    void testDeleteProduct() {
        // WHEN
        productRepository.delete(productEntity);

        // THEN
        assertFalse(productRepository.existsById(productEntity.getId()));
    }

}
