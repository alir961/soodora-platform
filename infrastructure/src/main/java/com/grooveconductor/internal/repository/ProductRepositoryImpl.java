package com.grooveconductor.internal.repository;

import com.grooveconductor.domain.command.CreateProductCommand;
import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.dto.UserDTO;
import com.grooveconductor.domain.exception.UnknownEmailException;
import com.grooveconductor.domain.exception.UnknownNameException;
import com.grooveconductor.domain.exception.UnknownSlugException;
import com.grooveconductor.domain.repository.ProductRepository;
import com.grooveconductor.internal.entity.ProductEntity;
import com.grooveconductor.internal.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.grooveconductor.internal.utils.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository repository;
    private final UserJpaRepository userRepository;

    protected ProductEntity findProductBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with slug :" + slug));
    }

    protected ProductEntity findProductByNameOrThrow(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new UnknownNameException("Unable to find product with name :" + name));
    }

    protected List<ProductEntity> findProductsByUserSlugOrThrow(String slug) {
        return repository.findProductsByUserSlug(slug)
            .orElseThrow(() -> new UnknownNameException("Unable to find product with id :" + slug));
    }

    @Override
    public ProductDTO save(CreateProductCommand command, UserDTO user) {
        ProductEntity entity = new ProductEntity();
        UserEntity userEntity = userRepository.findBySlug(user.slug())
            .orElseThrow(() -> new UnknownEmailException("Unable to find user '" + user.slug() + "'"));

        entity.setName(command.name());
        entity.setPrice(command.price());
        entity.setDescription(command.description());
        entity.setImageUrl(command.imageUrl());
        entity.setCategory(command.category());
        entity.setQuantity(command.quantity());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        entity.setUserSlug(userEntity.getSlug());
        return repository.save(entity).toDTO();
    }

    @Override
    public ProductDTO findBySlug(String slug) {
        return findProductBySlugOrThrow(slug).toDTO();
    }

    @Override
    public ProductDTO findByName(String name) {
        return findProductByNameOrThrow(name).toDTO();
    }

    @Override
    public List<ProductDTO> findAll() {
        return repository.findAll().stream()
                .map(ProductEntity::toDTO)
                .toList();
    }

    @Override
    public List<ProductDTO> findProductsByUserSlug(String slug) {
        return findProductsByUserSlugOrThrow(slug).stream()
            .map(ProductEntity::toDTO)
            .toList();
    }

    @Override
    public void deleteBySlug(String slug) {
        Optional<ProductEntity> product = repository.findBySlug(slug);

        repository.deleteById(product.orElseThrow().getId());
    }

    @Override
    public boolean isProductNameUsedByUser(String name, String userSlug) {
        List<ProductEntity> userProducts = findProductsByUserSlugOrThrow(userSlug);
        Stream<ProductEntity> productsOfUser = userProducts.stream();

        return productsOfUser.anyMatch(products -> products.getName().equalsIgnoreCase(name));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
