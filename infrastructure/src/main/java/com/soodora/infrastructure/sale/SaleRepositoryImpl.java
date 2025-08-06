package com.soodora.infrastructure.sale;

import com.soodora.domain.common.UnknownSlugException;
import com.soodora.domain.sale.SaleDTO;
import com.soodora.domain.sale.SaleRepository;
import com.soodora.domain.sale.SaveSaleCommand;
import com.soodora.domain.user.Identifier;
import com.soodora.infrastructure.product.ProductEntity;
import com.soodora.infrastructure.product.ProductJpaRepository;
import com.soodora.infrastructure.user.UserEntity;
import com.soodora.infrastructure.user.UserJpaRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import static com.soodora.infrastructure.common.SlugGenerator.generateSlug;

@Repository
@RequiredArgsConstructor
public class SaleRepositoryImpl implements SaleRepository {

    private final SaleJpaRepository repository;
    private final UserJpaRepository userRepository;
    private final ProductJpaRepository productRepository;

    @Override
    @Transactional
    public SaleDTO save(SaveSaleCommand sale) {
        SaleEntity entity = new SaleEntity();
        UserEntity userEntity = userRepository.findBySlug(sale.user().slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find user #" + sale.user().slug()));
        ProductEntity productEntity = productRepository.findBySlug(sale.product().slug())
                .orElseThrow(() -> new UnknownSlugException("Unable to find product#" + sale.product().slug()));

        entity.setProduct(productEntity);
        entity.setQuantitySold(sale.quantitySold());
        entity.setAmount(sale.amount());
        entity.setSlug(generateUniqueSlug());
        entity.setUser(userEntity);
        return repository.save(entity).toDTO();
    }

    @Override
    @Transactional
    public SaleDTO findBySlug(String slug) {
        return findSaleBySlugOrThrow(slug).toDTO();

    }

    @Override
    @Transactional
    public Page<SaleDTO> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(SaleEntity::toDTO);
    }

    @Override
    @Transactional
    public Page<SaleDTO> findUserSales(Identifier identifier, Pageable pageable) {
        return repository.findByUserSlug(identifier.getValue(), pageable)
                .map(SaleEntity::toDTO);
    }

    @Override
    @Transactional
    public void deleteSaleBySlug(String slug) {
        Optional<SaleEntity> sale = repository.findBySlug(slug);

        repository.deleteById(sale.orElseThrow().getId());
    }

    protected SaleEntity findSaleBySlugOrThrow(String slug) {
        return repository.findBySlug(slug)
                .orElseThrow(() -> new UnknownSlugException("Unable to find sale with slug: #" + slug));
    }

    private String generateUniqueSlug() {
        String slug = generateSlug();

        while (repository.findBySlug(slug).isPresent())
            slug = generateSlug();
        return slug;
    }
}
