package com.grooveconductor.infrastructure.product;

import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.product.CreateProductCommand;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.product.ProductRepository;
import com.grooveconductor.domain.product.UpdateProductCommand;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.infrastructure.common.DomainEntity;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository repository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public ProductDTO save(CreateProductCommand command, UserDTO user) {
        ProductEntity entity = new ProductEntity();
        UserEntity userEntity = userRepository.findBySlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + user.slug()));

        entity.setName(command.name());
        entity.setPrice(command.price());
        entity.setDescription(command.description());
        entity.setImageUrl(command.imageUrl());
        entity.setCategory(command.category());
        entity.setQuantity(command.quantity());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public ProductDTO update(UpdateProductCommand command, String slug) {
        ProductEntity existingProduct = findProductBySlugOrThrow(slug);

        if (command.getName() != null && !command.getName().isBlank()
                && !command.getName().equals(existingProduct.getName()))
            existingProduct.setName(command.getName());
        if (command.getDescription() != null && !command.getDescription().isBlank()
                && !command.getDescription().equals(existingProduct.getDescription()))
            existingProduct.setDescription(command.getDescription());
        if (command.getCategory() != null && !command.getCategory().isBlank()
                && !command.getCategory().equals(existingProduct.getCategory()))
            existingProduct.setCategory(command.getCategory());
        if (command.getImageUrl() != null && !command.getImageUrl().isBlank()
                && !command.getImageUrl().equals(existingProduct.getImageUrl()))
            existingProduct.setImageUrl(command.getImageUrl());
        if (command.getPrice() != 0 && command.getPrice() != existingProduct.getPrice())
            existingProduct.setPrice(command.getPrice());
        if (command.getQuantity() != 0 && command.getQuantity() != existingProduct.getQuantity())
            existingProduct.setQuantity(command.getPrice());

        return repository.save(existingProduct).toDTO();
    }

    @Override
    @Transactional
    public ProductDTO findBySlug(String slug) {
        return findProductBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public List<ProductDTO> findAll() {
        return repository.findAll().stream()
                .map(DomainEntity::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public void deleteProductBySlug(String slug) {
        Optional<ProductEntity> product = repository.findBySlug(slug);

        repository.deleteById(product.orElseThrow().getId());
    }

    @Override
    @Transactional
    public boolean isProductNameUsedByUser(String name, String userSlug) {
        List<ProductEntity> userProducts = findProductsByUserSlugOrThrow(userSlug);

        return userProducts.stream()
                .anyMatch(products -> products.getName().equalsIgnoreCase(name));
    }

    protected ProductEntity findProductBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with slug: #" + slug));
    }

    protected List<ProductEntity> findProductsByUserSlugOrThrow(String slug) {
        return repository.findProductsByUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with user slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
