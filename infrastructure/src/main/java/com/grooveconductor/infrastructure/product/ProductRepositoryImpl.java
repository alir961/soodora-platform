package com.grooveconductor.infrastructure.product;

import com.grooveconductor.domain.product.CreateProductCommand;
import com.grooveconductor.domain.product.ProductDTO;
import com.grooveconductor.domain.user.UserDTO;
import com.grooveconductor.domain.common.UnknownSlugException;
import com.grooveconductor.domain.product.ProductRepository;
import com.grooveconductor.infrastructure.user.UserEntity;
import com.grooveconductor.infrastructure.user.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.grooveconductor.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository repository;
    private final UserJpaRepository userRepository;

    protected ProductEntity findProductBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with slug: #" + slug));
    }

    protected List<ProductEntity> findProductsByUserSlugOrThrow(String slug) {
        return repository.findProductsByUserSlug(slug)
            .orElseThrow(() -> new UnknownSlugException("Unable to find product with user slug: #" + slug));
    }

    @Override
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
    public ProductDTO findBySlug(String slug) {
        return findProductBySlugOrThrow(slug).toDTO();
    }

    @Override
    public List<ProductDTO> findAll() {
        return repository.findAll().stream()
                .map(ProductEntity::toDTO)
                .toList();
    }

    @Override
    public void deleteProductBySlug(String slug) {
        Optional<ProductEntity> product = repository.findBySlug(slug);

        repository.deleteById(product.orElseThrow().getId());
    }

    @Override
    public boolean isProductNameUsedByUser(String name, String userSlug) {
        List<ProductEntity> userProducts = findProductsByUserSlugOrThrow(userSlug);

        return userProducts.stream()
                .anyMatch(products -> products.getName().equalsIgnoreCase(name));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
