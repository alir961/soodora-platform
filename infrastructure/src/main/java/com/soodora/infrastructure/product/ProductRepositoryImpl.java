package com.soodora.infrastructure.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.*;
import com.soodora.domain.user.User;
import com.soodora.domain.user.value.Identifier;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.DomainEntityAdapter;
import com.soodora.infrastructure.common.EntitySpecification;
import com.soodora.infrastructure.common.MoneyEmbeddable;
import com.soodora.infrastructure.sale.SaleJpaRepository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;
import static com.soodora.domain.product.ProductStatus.ACTIVE;
import static com.soodora.domain.product.ProductStatus.INACTIVE;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository repository;
    private final ArtistJpaRepository artistRepository;
    private final ImageStorageUploader imageStorageUploader;
    private final SaleJpaRepository saleRepository;

    @Override
    @Transactional
    public ProductDTO save(CreateProductCommand command, User user, FileData fileData) {
        ProductEntity entity = new ProductEntity();
        ArtistEntity artistEntity = artistRepository.findByUserSlug(user.getSlug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist with user slug #" + user.getSlug()));

        entity.setName(command.name());
        entity.setPrice(new MoneyEmbeddable(command.price(), command.currency()));
        entity.setDescription(command.description());
        if (fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            entity.setImageUrl(imageUrl);
        }
        entity.setCategory(command.category());
        entity.setQuantity(command.quantity());
        entity.setSlug(generateUniqueSlug());
        entity.setArtist(artistEntity);
        entity.setStatus(ACTIVE);
        return repository.save(entity).aggregate();
    }

    @Override
    @Transactional
    public ProductDTO update(UpdateProductCommand command, String slug, FileData fileData) {
        ProductEntity existingProduct = findProductBySlugOrThrow(slug);

        if (isNotNullOrBlank(command.name()))
            existingProduct.setName(command.name());
        if (isNotNullOrBlank(command.description()))
            existingProduct.setDescription(command.description());
        if (isNotNullOrBlank(command.category()))
            existingProduct.setCategory(command.category());
        if (fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            existingProduct.setImageUrl(imageUrl);
        }
        if (command.price().compareTo(BigDecimal.ZERO) > 0)
            existingProduct.setPrice(new MoneyEmbeddable(command.price(), command.currency()));
        if (command.quantity() != 0)
            existingProduct.setQuantity(command.quantity());

        return repository.save(existingProduct).aggregate();
    }

    @Override
    @Transactional
    public void updateQuantity(String slug, long quantity) {
        ProductEntity existingProduct = findProductBySlugOrThrow(slug);

        if (quantity >= 0)
            existingProduct.setQuantity(quantity);
        repository.save(existingProduct).aggregate();
    }

    @Override
    @Transactional
    public ProductDTO findBySlug(String slug) {
        return findProductBySlugOrThrow(slug).aggregate();
    }

    @Override
    @Transactional
    public Page<ProductDTO> findAll(Pageable pageable) {
        return repository.findByStatus(ACTIVE, pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<ProductDTO> findByFilter(ProductFilter filter, Pageable pageable) {
        EntitySpecification<ProductEntity> productSpecification = new ProductSpecification(filter);

        return repository.findAll(productSpecification.build(), pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public Page<ProductDTO> findUserProducts(Identifier identifier, Pageable pageable) {
        return repository.findByArtistUserSlug(identifier.value(), pageable)
                .map(DomainEntityAdapter::aggregate);
    }

    @Override
    @Transactional
    public void deleteProductBySlug(String slug) {
        ProductEntity product = findProductBySlugOrThrow(slug);
        boolean hasSales = saleRepository.existsByProduct(product);

        if (hasSales) {
            product.setStatus(INACTIVE);
            repository.save(product);
            return;
        }
        repository.delete(product);

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
        return repository.findProductsByArtistUserSlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with user slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }

}
