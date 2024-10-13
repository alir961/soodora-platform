package com.grooveconductor.internal.repository;

import com.grooveconductor.internal.InfrastructureTestConfiguration;
import com.grooveconductor.internal.entity.ProductEntity;
import com.grooveconductor.internal.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static java.util.UUID.randomUUID;

@DataJpaTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = InfrastructureTestConfiguration.class)
class ProductJpaRepositoryTest {

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private UserJpaRepository userRepository;

    private ProductEntity productEntity;

    @BeforeEach
    void setUp() {
        productEntity = new ProductEntity();
        productEntity.setId(randomUUID());
        productEntity.setSlug("product-slug");
        productEntity.setName("Test Product");
        productEntity.setDescription("Description");
        productEntity.setImageUrl("image-url");
        productEntity.setCategory("category");
        productEntity.setPrice(100);
        productEntity.setQuantity(10);

        productRepository.save(productEntity);
    }

    @Test
    void testFindBySlug_ProductExists() {
        // WHEN
        Optional<ProductEntity> foundProduct = productRepository.findBySlug("product-slug");

        // THEN
        assertTrue(foundProduct.isPresent());
        assertEquals("product-slug", foundProduct.get().getSlug());
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
        Optional<ProductEntity> foundProduct = productRepository.findByName("Test Product");

        // THEN
        assertTrue(foundProduct.isPresent());
        assertEquals("Test Product", foundProduct.get().getName());
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
        UserEntity user = new UserEntity();
        user.setSlug("user-slug");
        user.setUsername("username");
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setFirstname("John");
        user.setLastname("Doe");
        user = userRepository.save(user);

        ProductEntity product = new ProductEntity();
        product.setSlug("playlist-slug");
        product.setName("My Playlist");
        product.setDescription("A test playlist");
        product.setUser(user);
        productRepository.save(product);

        // WHEN
        Optional<List<ProductEntity>> foundProducts = productRepository.findProductsByUserSlug("user-slug");

        // THEN
        assertTrue(foundProducts.isPresent());
        assertFalse(foundProducts.get().isEmpty());
    }

    @Test
    void testFindProductsByUserSlug_NoProductsExist() {
        // WHEN
        Optional<List<ProductEntity>> foundProducts = productRepository.findProductsByUserSlug("unknown-user-slug");

        // THEN
        assertTrue(foundProducts.get().isEmpty());
    }

    @Test
    void testSaveProduct() {
        // GIVEN
        ProductEntity newProduct = new ProductEntity();
        newProduct.setId(randomUUID());
        newProduct.setSlug("new-product-slug");
        newProduct.setName("New Product");
        newProduct.setDescription("New Description");
        newProduct.setImageUrl("new-image-url");
        newProduct.setCategory("new-category");
        newProduct.setPrice(200);
        newProduct.setQuantity(5);

        // WHEN
        ProductEntity savedProduct = productRepository.save(newProduct);

        // THEN
        assertNotNull(savedProduct.getId());
        assertEquals("new-product-slug", savedProduct.getSlug());
        assertEquals("New Product", savedProduct.getName());
    }

    @Test
    void testDeleteProduct() {
        // WHEN
        productRepository.delete(productEntity);

        // THEN
        Optional<ProductEntity> foundProduct = productRepository.findById(productEntity.getId());
        assertFalse(foundProduct.isPresent());
    }
}
