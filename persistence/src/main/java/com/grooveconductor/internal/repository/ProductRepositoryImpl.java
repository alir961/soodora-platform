package com.grooveconductor.internal.repository;

import com.grooveconductor.domain.command.CreateProductCommand;
import com.grooveconductor.domain.dto.ProductDTO;
import com.grooveconductor.domain.exception.UnknownSlugException;
import com.grooveconductor.domain.repository.ProductRepository;
import com.grooveconductor.internal.entity.ProductEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository repository;

    protected ProductEntity findProductBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with slug :" + slug));
    }

    protected ProductEntity findProductByNameOrThrow(String name) {
        return repository.findByName(name)
                .orElseThrow(() -> new UnknownSlugException("Unable to find product with name :" + name));
    }

    @Override
    public ProductDTO save(CreateProductCommand command, String slug) {
        ProductEntity entity = new ProductEntity();

        entity.setSlug(slug);
        entity.setName(command.name());
        entity.setPrice(command.price());
        entity.setDescription(command.description());
        entity.setImageUrl(command.imageUrl());
        entity.setCategory(command.category());
        entity.setQuantity(command.quantity());
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
    public void deleteBySlug(String slug) {
        Optional<ProductEntity> product = repository.findBySlug(slug);

        repository.deleteById(product.get().getId());
    }

    @Override
    public boolean isSlugUsed(String slug) {
        try {
            repository.findBySlug(slug);
            return true;
        } catch (UnknownSlugException exception) {
            return false;
        }
    }

    @Override
    public boolean isNameUsed(String name) {
        try {
            findByName(name);
            return true;
        } catch (UnknownSlugException exception) {
            return false;
        }
    }

}
