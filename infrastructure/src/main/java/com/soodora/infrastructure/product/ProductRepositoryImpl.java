package com.soodora.infrastructure.product;

import com.soodora.domain.cdn.FileData;
import com.soodora.domain.cdn.ImageStorageUploader;
import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.product.CreateProductCommand;
import com.soodora.domain.product.ProductDTO;
import com.soodora.domain.product.ProductFilter;
import com.soodora.domain.product.ProductRepository;
import com.soodora.domain.product.UpdateProductCommand;
import com.soodora.domain.user.Identifier;
import com.soodora.domain.user.UserDTO;
import com.soodora.infrastructure.artist.ArtistEntity;
import com.soodora.infrastructure.artist.ArtistJpaRepository;
import com.soodora.infrastructure.common.Money;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.domain.common.SlugGenerator.generateSlug;
import static com.soodora.domain.common.StringValidator.isNotNullOrBlank;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository repository;
    private final ArtistJpaRepository artistRepository;
    private final ImageStorageUploader imageStorageUploader;

    @Override
    @Transactional
    public ProductDTO save(CreateProductCommand command, UserDTO user, FileData fileData) {
        ProductEntity entity = new ProductEntity();
        ArtistEntity artistEntity = artistRepository.findByUserSlug(user.slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find artist with user slug #" + user.slug()));

        entity.setName(command.name());
        entity.setPrice(Money.of(command.price(), command.currency()));
        entity.setDescription(command.description());
        if (fileData != null && fileData.content() != null) {
            String imageUrl = imageStorageUploader.upload(fileData);
            entity.setImageUrl(imageUrl);
        }
        entity.setCategory(command.category());
        entity.setQuantity(command.quantity());
        entity.setSlug(generateUniqueSlug());
        entity.setArtist(artistEntity);
        return repository.save(entity).toDTO();
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
        if (command.price() != 0)
            existingProduct.setPrice(Money.of(command.price(), command.currency()));
        if (command.quantity() != 0)
            existingProduct.setQuantity(command.quantity());

        return repository.save(existingProduct).toDTO();
    }

    @Override
    @Transactional
    public void updateQuantity(String slug, long quantity) {
        ProductEntity existingProduct = findProductBySlugOrThrow(slug);

        if (quantity >= 0)
            existingProduct.setQuantity(quantity);
        repository.save(existingProduct).toDTO();
    }

    @Override
    @Transactional
    public ProductDTO findBySlug(String slug) {
        return findProductBySlugOrThrow(slug).toDTO();
    }

    @Override
    @Transactional
    public Page<ProductDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(ProductEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<ProductDTO> findByFilter(ProductFilter filter, Pageable pageable) {
        return repository.findAll(ProductSpecification.build(filter), pageable)
                .map(ProductEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<ProductDTO> findUserProducts(Identifier identifier, Pageable pageable) {
        return repository.findByArtistUserSlug(identifier.getValue(), pageable)
                .map(ProductEntity::toDTO);
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
